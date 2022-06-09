package devilSpiderX.server.webServer.service;

import devilSpiderX.server.webServer.service.information.CPU;
import devilSpiderX.server.webServer.service.information.Disk;
import devilSpiderX.server.webServer.service.information.Memory;
import devilSpiderX.server.webServer.service.information.Network;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyServerInfo {
    public static final MyServerInfo serverInfo = new MyServerInfo();
    private static final int coolDownTime = 1000;
    private final CD_Thread cdThread;
    private static int cdThread_count = 0;
    private boolean coolDown = false;
    private final Object cd_lock = new Object();
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

    public MyServerInfo() {
        SystemInfo info = new SystemInfo();
        HardwareAbstractionLayer hw = info.getHardware();
        oldTicks = hw.getProcessor().getSystemCpuLoadTicks();
        double temp = hw.getSensors().getCpuTemperature();
        if (temp == 0 || Double.isNaN(temp)) {
            isTemperatureDetectable = false;
        }

        int diskSize = info.getOperatingSystem().getFileSystem().getFileStores().size();
        for (int i = 0; i < diskSize; i++) {
            disks.add(new Disk());
        }

        List<NetworkIF> nIfs = hw.getNetworkIFs();
        for (NetworkIF nIf : nIfs) {
            if (nIf.getIfOperStatus().equals(NetworkIF.IfOperStatus.UP)) {
                networks.add(new Network(nIf.getName(), nIf.getTimeStamp(), nIf.getBytesSent(), nIf.getBytesRecv()));
            }
        }

        cdThread = new CD_Thread();
        cdThread.start();
    }

    public CPU getCPU() {
        return cpu;
    }

    public Memory getMemory() {
        return memory;
    }

    public List<Disk> getDisks() {
        return disks;
    }

    public List<Network> getNetworks() {
        return networks;
    }

    public MyServerInfo update() {
        synchronized (cd_lock) {
            if (!coolDown) {
                _update();
            }
        }
        return this;
    }

    private void _update() {
        SystemInfo info = new SystemInfo();
        HardwareAbstractionLayer hw = info.getHardware();
        Sensors sensors = hw.getSensors();
        setCPUInfo(hw.getProcessor(), sensors);
        setMemoryInfo(hw.getMemory());
        setDisksInfo(info.getOperatingSystem());
        HashMap<String, NetworkIF> nIfMap = new HashMap<>();
        for (NetworkIF nIf : hw.getNetworkIFs()) {
            if (nIf.getIfOperStatus().equals(NetworkIF.IfOperStatus.UP)) {
                nIfMap.put(nIf.getName(), nIf);
            }
        }
        setNetworkInfo(nIfMap);
        coolDown = true;
        cdThread.activate();
    }

    /**
     * 设置CPU信息
     */
    private void setCPUInfo(CentralProcessor processor, Sensors sensors) {
        // CPU信息
        CentralProcessor.ProcessorIdentifier cp_pi = processor.getProcessorIdentifier();
        cpu.setName(cp_pi.getName())
                .setPhysicalNum(processor.getPhysicalProcessorCount())
                .setLogicalNum(processor.getLogicalProcessorCount())
                .setUsedRate(processor.getSystemCpuLoadBetweenTicks(oldTicks))
                .set64bit(cp_pi.isCpu64bit());
        if (isTemperatureDetectable) {
            cpu.setCpuTemperature(sensors.getCpuTemperature());
        }
        oldTicks = processor.getSystemCpuLoadTicks();
    }

    /**
     * 设置内存信息
     */
    private void setMemoryInfo(GlobalMemory memory) {
        this.memory.setTotal(memory.getTotal())
                .setUsed(memory.getTotal() - memory.getAvailable())
                .setFree(memory.getAvailable());
    }

    /**
     * 设置磁盘信息
     */
    private void setDisksInfo(OperatingSystem os) {
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsList = fileSystem.getFileStores();
        if (disks.size() != fsList.size()) {
            disks.clear();
            for (int i = 0; i < fsList.size(); i++) {
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
    }

    public void setNetworkInfo(HashMap<String, NetworkIF> nIfMap) {
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

    private class CD_Thread extends Thread {

        public CD_Thread() {
            super("MyServerInfo_cd_thread_" + (cdThread_count++));
            setDaemon(true);
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                Util.sleep(coolDownTime);
                synchronized (cd_lock) {
                    coolDown = false;
                    try {
                        cd_lock.wait();
                    } catch (InterruptedException e) {
                        interrupt();
                    }
                }
            }
        }

        public void activate() {
            cd_lock.notify();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            serverInfo.update();
            CPU cpu = serverInfo.getCPU();
            Memory memory = serverInfo.getMemory();
            List<Disk> disks = serverInfo.getDisks();
            List<Network> networks = serverInfo.getNetworks();

            System.out.println(cpu);
            System.out.println("CPU Free Percent:" + cpu.getFreePercent());
            System.out.println("CPU Used Percent:" + cpu.getUsedPercent());

            System.out.println();
            System.out.println(memory);
            System.out.println("Memory Total Size:" + memory.getTotalStr());
            System.out.println("Memory Used Size:" + memory.getUsedStr());
            System.out.println("Memory Free Size:" + memory.getFreeStr());
            System.out.println("Memory Usage:" + memory.getUsage());
            System.out.println();
            System.out.println(disks);
            int j = 0;
            for (Disk disk : disks) {
                System.out.println("Disk" + j + " Total Size:" + disk.getTotalStr());
                System.out.println("Disk" + j + " Used Size:" + disk.getUsedStr());
                System.out.println("Disk" + j + " Free Size:" + disk.getFreeStr());
                System.out.println("Disk" + j + " Usage:" + disk.getUsage());
                j++;
            }
            System.out.println();
            for (Network network : networks) {
                System.out.println(network);
                System.out.printf("%s:上传速度 %s 下载速度 %s\n", network.getName(), network.getUploadSpeedStr(),
                        network.getDownloadSpeedStr());
            }
            System.out.println("--------------------------------------------------");
            Util.sleep(1000);
        }
    }
}
