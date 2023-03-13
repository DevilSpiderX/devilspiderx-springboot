package devilSpiderX.server.webServer.module.serverInfo.service.impl;

import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import devilSpiderX.server.webServer.module.serverInfo.statistic.*;
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
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ServerInfoServiceImpl implements ServerInfoService {
    private static final int coolDownTime = 1000;
    private static final AtomicInteger updateThreadCount = new AtomicInteger();
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
                networks.add(new Network(nIf.getName(), nIf.getTimeStamp(), nIf.getBytesSent(), nIf.getBytesRecv()));
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
    public List<Disk> getDisks() {
        this.update();
        synchronized (disks) {
            return disks;
        }
    }

    @Override
    public List<Network> getNetworks() {
        this.update();
        synchronized (networks) {
            return networks;
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
                NetworkIF nif = nIfMap.get(network.getName());
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
                network.setIPv4addr(nif.getIPv4addr());
                network.setIPv6addr(nif.getIPv6addr());

                nIfMap.remove(network.getName(), nif);
            }
            if (nIfMap.size() != 0) {
                nIfMap.forEach((k, v) -> networks.add(new Network(v.getName(), v.getTimeStamp(), v.getBytesSent(),
                        v.getBytesRecv())));
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
        long total = memory.getTotal();
        long used = memory.getUsed();
        long free = memory.getFree();

        return Map.of(
                "total", total,
                "used", used,
                "free", free
        );
    }

    @Override
    public Map<String, Serializable> constructDiskObject(Disk disk) {
        long total = disk.getTotal();
        long free = disk.getFree();
        long used = disk.getUsed();

        return Map.of(
                "label", disk.getLabel(),
                "mount", disk.getMount(),
                "fSType", disk.getFSType(),
                "name", disk.getName(),
                "total", total,
                "free", free,
                "used", used
        );
    }

    @Override
    public Map<String, Serializable> constructNetworkObject(Network network) {
        long uploadSpeed = network.getUploadSpeed();
        long downloadSpeed = network.getDownloadSpeed();

        return Map.of(
                "uploadSpeed", uploadSpeed,
                "downloadSpeed", downloadSpeed
        );
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
        public UpdateThread() {
            super("ServerInfo_update_thread_" + (updateThreadCount.getAndIncrement()));
            setDaemon(true);
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
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
            }
        }

        public synchronized void activate() {
            this.notify();
        }
    }
}
