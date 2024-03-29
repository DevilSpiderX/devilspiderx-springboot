package devilSpiderX.server.webServer.module.serverInfo.service.impl;

import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import devilSpiderX.server.webServer.module.serverInfo.statistic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class ServerInfoServiceImpl implements ServerInfoService {
    private static final int coolDownTime = 1000;
    private final UpdateThread updateThread = new UpdateThread();
    private final AtomicBoolean allowUpdate = new AtomicBoolean();
    private final HardwareAbstractionLayer hw;
    private final OperatingSystem os;
    private long[] oldTicks;
    private boolean isTemperatureDetectable = true;
    /**
     * CPU相关信息
     */
    private final CPU cpu = new CPU();
    /**
     * 內存相关信息
     */
    private final Memory memory = new Memory();
    /**
     * 磁盘相关信息
     */
    private final List<Disk> disks = new ArrayList<>();
    /**
     * 网络相关信息
     */
    private final List<Network> networks = new ArrayList<>();
    /**
     * 系统相关信息
     */
    private final CurrentOS currentOS = new CurrentOS();

    public ServerInfoServiceImpl() {
        SystemInfo info = new SystemInfo();
        hw = info.getHardware();
        os = info.getOperatingSystem();
        oldTicks = hw.getProcessor().getSystemCpuLoadTicks();
        double temperature = hw.getSensors().getCpuTemperature();
        if (temperature == 0 || Double.isNaN(temperature)) {
            isTemperatureDetectable = false;
        }

        for (int i = 0; i < os.getFileSystem().getFileStores().size(); i++) {
            disks.add(new Disk());
        }

        for (NetworkIF nIf : hw.getNetworkIFs()) {
            if (nIf.getIfOperStatus().equals(NetworkIF.IfOperStatus.UP)) {
                networks.add(new Network(
                        nIf.getName(),
                        nIf.getDisplayName(),
                        nIf.getMacaddr(),
                        nIf.getTimeStamp(),
                        nIf.getBytesSent(),
                        nIf.getBytesRecv(),
                        nIf.getIPv4addr(),
                        nIf.getIPv6addr()
                ));
            }
        }

        updateThread.start();
    }

    public void update() {
        if (allowUpdate.compareAndSet(true, false)) {
            updateThread.activate();
        }
    }

    @Override
    public CPU getCPU() {
        this.update();
        synchronized (cpu) {
            return cpu;
        }
    }

    @Override
    public Memory getMemory() {
        this.update();
        synchronized (memory) {
            return memory;
        }
    }

    @Override
    public Disk[] getDisks() {
        this.update();
        synchronized (disks) {
            return disks.toArray(new Disk[0]);
        }
    }

    @Override
    public Network[] getNetworks() {
        this.update();
        synchronized (networks) {
            return networks.toArray(new Network[0]);
        }
    }

    @Override
    public CurrentOS getCurrentOS() {
        this.update();
        synchronized (currentOS) {
            return currentOS;
        }
    }

    /**
     * 设置CPU信息
     */
    private void setCPUInfo() {
        CentralProcessor processor = hw.getProcessor();
        CentralProcessor.ProcessorIdentifier processorIdentifier = processor.getProcessorIdentifier();
        synchronized (cpu) {
            cpu.setName(processorIdentifier.getName())
                    .setPhysicalNum(processor.getPhysicalProcessorCount())
                    .setLogicalNum(processor.getLogicalProcessorCount())
                    .setUsedRate(processor.getSystemCpuLoadBetweenTicks(oldTicks))
                    .setA64bit(processorIdentifier.isCpu64bit());
            if (isTemperatureDetectable) {
                cpu.setTemperature(hw.getSensors().getCpuTemperature());
            }
        }
        oldTicks = processor.getSystemCpuLoadTicks();
    }

    /**
     * 设置内存信息
     */
    private void setMemoryInfo() {
        GlobalMemory gMemory = hw.getMemory();
        long total = gMemory.getTotal();
        synchronized (memory) {
            memory.setTotal(total)
                    .setUsed(total - gMemory.getAvailable())
                    .setFree(gMemory.getAvailable());
        }
    }

    /**
     * 设置磁盘信息
     */
    private void setDisksInfo() {
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsList = fileSystem.getFileStores();
        synchronized (disks) {
            int n = disks.size() - fsList.size();
            if (n > 0) {
                disks.subList(0, n).clear();
            } else if (n < 0) {
                for (int i = 0; i < -n; i++) {
                    disks.add(new Disk());
                }
            }
            for (int i = 0; i < fsList.size(); i++) {
                OSFileStore fs = fsList.get(i);
                long free = fs.getUsableSpace();
                long total = fs.getTotalSpace();
                long used = total - free;
                disks.get(i).setLabel(fs.getLabel())
                        .setMount(fs.getMount())
                        .setFSType(fs.getType())
                        .setName(fs.getName())
                        .setTotal(total)
                        .setFree(free)
                        .setUsed(used);
            }
            disks.sort(Comparator.naturalOrder());
        }
    }

    /**
     * 设置网络信息
     */
    public void setNetworkInfo() {
        HashMap<String, NetworkIF> nIfMap = new HashMap<>();
        for (NetworkIF nIf : hw.getNetworkIFs()) {
            if (nIf.getIfOperStatus().equals(NetworkIF.IfOperStatus.UP)) {
                nIfMap.put(nIf.getName(), nIf);
            }
        }
        synchronized (networks) {
            for (Network network : networks) {
                NetworkIF nif = nIfMap.remove(network.getName());
                if (nif == null) {
                    networks.remove(network);
                    continue;
                }
                long vTime = nif.getTimeStamp() - network.getTimeStamp();

                network.setUploadSpeed((nif.getBytesSent() - network.getBytesSent()) * 1000 / vTime);
                network.setDownloadSpeed((nif.getBytesRecv() - network.getBytesRecv()) * 1000 / vTime);

                network.setTimeStamp(nif.getTimeStamp());
                network.setBytesSent(nif.getBytesSent());
                network.setBytesRecv(nif.getBytesRecv());
                network.setIPv4Addr(nif.getIPv4addr());
                network.setIPv6Addr(nif.getIPv6addr());
            }
            if (!nIfMap.isEmpty()) {
                nIfMap.forEach((k, v) -> networks.add(new Network(
                        v.getName(),
                        v.getDisplayName(),
                        v.getMacaddr(),
                        v.getTimeStamp(),
                        v.getBytesSent(),
                        v.getBytesRecv(),
                        v.getIPv4addr(),
                        v.getIPv6addr()
                )));
            }
        }
    }

    public void setCurrentOSInfo() {
        synchronized (currentOS) {
            currentOS.setName(os.getFamily());
            currentOS.setBitness(os.getBitness());
            currentOS.setProcessCount(os.getProcessCount());
        }
    }

    @Override
    public Map<String, Serializable> constructCpuObject(CPU cpu) {
        return Map.of(
                "name", cpu.getName(),
                "physicalNum", cpu.getPhysicalNum(),
                "logicalNum", cpu.getLogicalNum(),
                "usedRate", cpu.getUsedRate(),
                "is64bit", cpu.isA64bit(),
                "cpuTemperature", cpu.getTemperature()
        );
    }

    @Override
    public Map<String, Serializable> constructMemoryObject(Memory memory) {
        return Map.of(
                "total", memory.getTotal(),
                "used", memory.getUsed(),
                "free", memory.getFree()
        );
    }

    @Override
    public Map<String, Serializable> constructDiskObject(Disk disk) {
        return Map.of(
                "label", disk.getLabel(),
                "mount", disk.getMount(),
                "fSType", disk.getFSType(),
                "name", disk.getName(),
                "total", disk.getTotal(),
                "free", disk.getFree(),
                "used", disk.getUsed()
        );
    }

    @Override
    public Map<String, Serializable> constructNetworkObject(Network network) {
        final Map<String, Serializable> result = new HashMap<>();
        result.put("name", network.getName());
        result.put("displayName", network.getDisplayName());
        result.put("macAddr", network.getMacAddr());
        result.put("bytesSent", network.getBytesSent());
        result.put("bytesRecv", network.getBytesRecv());
        result.put("uploadSpeed", network.getUploadSpeed());
        result.put("downloadSpeed", network.getDownloadSpeed());
        result.put("IPv4addr", network.getIPv4Addr());
        result.put("IPv6addr", network.getIPv6Addr());
        return result;
    }

    @Override
    public Map<String, Serializable> constructCurrentOSObject(CurrentOS currentOS) {
        return Map.of(
                "name", currentOS.getName(),
                "bitness", currentOS.getBitness(),
                "processCount", currentOS.getProcessCount()
        );
    }

    private class UpdateThread extends Thread {
        private final Logger logger = LoggerFactory.getLogger(UpdateThread.class);

        public UpdateThread() {
            super("ServerInfo_update_thread");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    setCPUInfo();
                    setMemoryInfo();
                    setDisksInfo();
                    setNetworkInfo();
                    setCurrentOSInfo();
                    Util.sleep(coolDownTime);
                    allowUpdate.set(true);
                    synchronized (this) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }

        public synchronized void activate() {
            this.notify();
        }
    }
}
