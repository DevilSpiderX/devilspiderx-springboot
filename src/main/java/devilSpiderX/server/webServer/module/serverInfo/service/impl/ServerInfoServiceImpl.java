package devilSpiderX.server.webServer.module.serverInfo.service.impl;

import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import devilSpiderX.server.webServer.module.serverInfo.statistic.*;
import devilSpiderX.server.webServer.module.serverInfo.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class ServerInfoServiceImpl implements ServerInfoService {
    private static final int COOL_DOWN_TIME = 1000;
    private final UpdateThread updateThread = new UpdateThread();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
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
        lock.writeLock()
                .lock();
        try {
            final var info = new SystemInfo();
            hw = info.getHardware();
            os = info.getOperatingSystem();
            oldTicks = hw.getProcessor()
                    .getSystemCpuLoadTicks();
            final var temperature = hw.getSensors()
                    .getCpuTemperature();
            if (temperature == 0 || Double.isNaN(temperature)) {
                isTemperatureDetectable = false;
            }

            for (int i = 0; i < os.getFileSystem()
                    .getFileStores()
                    .size(); i++) {
                disks.add(new Disk());
            }

            for (NetworkIF nIf : hw.getNetworkIFs()) {
                if (nIf.getIfOperStatus()
                        .equals(NetworkIF.IfOperStatus.UP)) {
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
        } finally {
            lock.writeLock()
                    .unlock();
        }
        updateThread.start();
    }

    public void update() {
        updateThread.activate();
    }

    @Override
    public CPU getCPU() {
        this.update();
        lock.readLock()
                .lock();
        try {
            return cpu;
        } finally {
            lock.readLock()
                    .unlock();
        }
    }

    @Override
    public Memory getMemory() {
        this.update();
        lock.readLock()
                .lock();
        try {
            return memory;
        } finally {
            lock.readLock()
                    .unlock();
        }
    }

    @Override
    public Disk[] getDisks() {
        this.update();
        lock.readLock()
                .lock();
        try {
            return disks.toArray(new Disk[0]);
        } finally {
            lock.readLock()
                    .unlock();
        }
    }

    @Override
    public Network[] getNetworks() {
        this.update();
        lock.readLock()
                .lock();
        try {
            return networks.toArray(new Network[0]);
        } finally {
            lock.readLock()
                    .unlock();
        }
    }

    @Override
    public CurrentOS getCurrentOS() {
        this.update();
        lock.readLock()
                .lock();
        try {
            return currentOS;
        } finally {
            lock.readLock()
                    .unlock();
        }
    }

    /**
     * 设置CPU信息
     */
    private void setCPUInfo() {
        final var processor = hw.getProcessor();
        final var processorIdentifier = processor.getProcessorIdentifier();
        lock.writeLock()
                .lock();
        try {
            cpu.setName(processorIdentifier.getName())
                    .setPhysicalNum(processor.getPhysicalProcessorCount())
                    .setLogicalNum(processor.getLogicalProcessorCount())
                    .setUsedRate(processor.getSystemCpuLoadBetweenTicks(oldTicks))
                    .setA64bit(processorIdentifier.isCpu64bit());
            if (isTemperatureDetectable) {
                cpu.setTemperature(hw.getSensors()
                        .getCpuTemperature());
            }
        } finally {
            lock.writeLock()
                    .unlock();
        }
        oldTicks = processor.getSystemCpuLoadTicks();
    }

    /**
     * 设置内存信息
     */
    private void setMemoryInfo() {
        final var gMemory = hw.getMemory();
        final var total = gMemory.getTotal();
        lock.writeLock()
                .lock();
        try {
            memory.setTotal(total)
                    .setUsed(total - gMemory.getAvailable())
                    .setFree(gMemory.getAvailable());
        } finally {
            lock.writeLock()
                    .unlock();
        }
    }

    /**
     * 设置磁盘信息
     */
    private void setDisksInfo() {
        final var fileSystem = os.getFileSystem();
        final List<OSFileStore> fsList = fileSystem.getFileStores();
        lock.writeLock()
                .lock();
        try {
            final int n = disks.size() - fsList.size();
            if (n > 0) {
                disks.subList(0, n)
                        .clear();
            } else if (n < 0) {
                for (int i = 0; i < -n; i++) {
                    disks.add(new Disk());
                }
            }
            for (int i = 0; i < fsList.size(); i++) {
                final var fs = fsList.get(i);
                final var free = fs.getUsableSpace();
                final var total = fs.getTotalSpace();
                final var used = total - free;
                disks.get(i)
                        .setLabel(fs.getLabel())
                        .setMount(fs.getMount())
                        .setFSType(fs.getType())
                        .setName(fs.getName())
                        .setTotal(total)
                        .setFree(free)
                        .setUsed(used);
            }
            disks.sort(Comparator.naturalOrder());
        } finally {
            lock.writeLock()
                    .unlock();
        }
    }

    /**
     * 设置网络信息
     */
    public void setNetworkInfo() {
        final Map<String, NetworkIF> nIfMap = new HashMap<>();
        for (final var nIf : hw.getNetworkIFs()) {
            if (nIf.getIfOperStatus()
                    .equals(NetworkIF.IfOperStatus.UP)) {
                nIfMap.put(nIf.getName(), nIf);
            }
        }
        lock.writeLock()
                .lock();
        try {
            final List<Network> removedList = new ArrayList<>();
            for (final var network : networks) {
                final var nif = nIfMap.remove(network.getName());
                if (nif == null) {
                    removedList.add(network);
                    continue;
                }
                final var vTime = nif.getTimeStamp() - network.getTimeStamp();

                network.setUploadSpeed((nif.getBytesSent() - network.getBytesSent()) * 1000 / vTime);
                network.setDownloadSpeed((nif.getBytesRecv() - network.getBytesRecv()) * 1000 / vTime);

                network.setTimeStamp(nif.getTimeStamp());
                network.setBytesSent(nif.getBytesSent());
                network.setBytesRecv(nif.getBytesRecv());
                network.setIPv4Addr(nif.getIPv4addr());
                network.setIPv6Addr(nif.getIPv6addr());
            }
            networks.removeAll(removedList);

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
        } finally {
            lock.writeLock()
                    .unlock();
        }
    }

    public void setCurrentOSInfo() {
        lock.writeLock()
                .lock();
        try {
            currentOS.setName(os.getFamily());
            currentOS.setBitness(os.getBitness());
            currentOS.setProcessCount(os.getProcessCount());
        } finally {
            lock.writeLock()
                    .unlock();
        }
    }

    @Override
    public CPUVo constructCpuObject(CPU cpu) {
        return new CPUVo(
                cpu.getName(),
                cpu.getPhysicalNum(),
                cpu.getLogicalNum(),
                cpu.getUsedRate(),
                cpu.isA64bit(),
                cpu.getTemperature()
        );
    }

    @Override
    public MemoryVo constructMemoryObject(Memory memory) {
        return new MemoryVo(
                memory.getTotal(),
                memory.getUsed(),
                memory.getFree()
        );
    }

    @Override
    public DiskVo constructDiskObject(Disk disk) {
        return new DiskVo(
                disk.getLabel(),
                disk.getMount(),
                disk.getFSType(),
                disk.getName(),
                disk.getTotal(),
                disk.getFree(),
                disk.getUsed()
        );
    }

    @Override
    public NetworkVo constructNetworkObject(Network network) {
        return new NetworkVo(
                network.getName(),
                network.getDisplayName(),
                network.getMacAddr(),
                network.getBytesSent(),
                network.getBytesRecv(),
                network.getUploadSpeed(),
                network.getDownloadSpeed(),
                network.getIPv4Addr(),
                network.getIPv6Addr()
        );
    }

    @Override
    public CurrentOSVo constructCurrentOSObject(CurrentOS currentOS) {
        return new CurrentOSVo(
                currentOS.getName(),
                currentOS.getBitness(),
                currentOS.getProcessCount()
        );
    }

    @Override
    public ServerInfoVo getServerInfo() {
        final var cpu = this.constructCpuObject(this.getCPU());

        final var memory = this.constructMemoryObject(this.getMemory());

        final var diskArray = this.getDisks();
        final var disks = new ArrayList<DiskVo>(diskArray.length);
        for (var disk : diskArray) {
            disks.add(this.constructDiskObject(disk));
        }

        final var networkArray = this.getNetworks();
        final var networks = new ArrayList<NetworkVo>(networkArray.length);
        for (var network : networkArray) {
            networks.add(this.constructNetworkObject(network));
        }

        final var currentOS = this.constructCurrentOSObject(this.getCurrentOS());

        return new ServerInfoVo(
                cpu,
                memory,
                disks,
                networks,
                currentOS
        );
    }

    private class UpdateThread extends Thread {
        private final Logger logger = LoggerFactory.getLogger(UpdateThread.class);
        private final AtomicBoolean allowUpdate = new AtomicBoolean();

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
                    Util.sleep(COOL_DOWN_TIME);
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

        public void activate() {
            if (!allowUpdate.compareAndSet(true, false)) {
                return;
            }
            synchronized (this) {
                this.notify();
            }
        }
    }
}
