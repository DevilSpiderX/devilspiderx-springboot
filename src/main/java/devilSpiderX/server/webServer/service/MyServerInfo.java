package devilSpiderX.server.webServer.service;

import devilSpiderX.server.webServer.service.information.CPU;
import devilSpiderX.server.webServer.service.information.Disk;
import devilSpiderX.server.webServer.service.information.Memory;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.util.Arrays;
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
    private final Disk[] disks;

    public MyServerInfo() {
        SystemInfo info = new SystemInfo();
        oldTicks = info.getHardware().getProcessor().getSystemCpuLoadTicks();
        double temp = info.getHardware().getSensors().getCpuTemperature();
        if (temp == 0 || Double.isNaN(temp)) {
            isTemperatureDetectable = false;
        }

        int diskSize = info.getOperatingSystem().getFileSystem().getFileStores().size();
        disks = new Disk[diskSize];
        for (int i = 0; i < diskSize; i++) {
            disks[i] = new Disk();
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

    public Disk[] getDisks() {
        return disks;
    }

    public MyServerInfo update() {
        synchronized (cd_lock) {
            if (!coolDown) {
                SystemInfo info = new SystemInfo();
                HardwareAbstractionLayer hw = info.getHardware();
                Sensors sensors = hw.getSensors();
                setCPUInfo(hw.getProcessor(), sensors);
                setMemoryInfo(hw.getMemory());
                setDisks(info.getOperatingSystem());
                coolDown = true;
                cdThread.activate();
            }
        }
        return this;
    }

    /**
     * 设置CPU信息
     */
    private void setCPUInfo(CentralProcessor processor, Sensors sensors) {
        // CPU信息
        CentralProcessor.ProcessorIdentifier cp_pi = processor.getProcessorIdentifier();
        cpu.setName(cp_pi.getName());
        cpu.setPhysicalNum(processor.getPhysicalProcessorCount());
        cpu.setLogicalNum(processor.getLogicalProcessorCount());
        cpu.setUsedRate(processor.getSystemCpuLoadBetweenTicks(oldTicks));
        cpu.set64bit(cp_pi.isCpu64bit());
        if (isTemperatureDetectable) {
            cpu.setCpuTemperature(sensors.getCpuTemperature());
        }
        oldTicks = processor.getSystemCpuLoadTicks();
    }

    /**
     * 设置内存信息
     */
    private void setMemoryInfo(GlobalMemory memory) {
        this.memory.setTotal(memory.getTotal());
        this.memory.setUsed(memory.getTotal() - memory.getAvailable());
        this.memory.setFree(memory.getAvailable());
    }

    /**
     * 设置磁盘信息
     */
    private void setDisks(OperatingSystem os) {
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsList = fileSystem.getFileStores();
        for (int i = 0; i < fsList.size(); i++) {
            OSFileStore fs = fsList.get(i);
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            Disk disk = disks[i];
            disk.setLabel(fs.getLabel());
            disk.setDir(fs.getMount());
            disk.setFSType(fs.getType());
            disk.setName(fs.getName());
            disk.setTotal(total);
            disk.setFree(free);
            disk.setUsed(used);
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
        for (int i = 0; i < 2; i++) {
            serverInfo.update();
            CPU cpu = serverInfo.getCPU();
            Memory memory = serverInfo.getMemory();
            Disk[] disks = serverInfo.getDisks();

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
            System.out.println(Arrays.toString(disks));
            int j = 0;
            for (Disk disk : disks) {
                System.out.println("Disk" + j + " Total Size:" + disk.getTotalStr());
                System.out.println("Disk" + j + " Used Size:" + disk.getUsedStr());
                System.out.println("Disk" + j + " Free Size:" + disk.getFreeStr());
                System.out.println("Disk" + j + " Usage:" + disk.getUsage());
                j++;
            }
            System.out.println("--------------------------------------------------");
            Util.sleep(1000);
        }
    }
}
