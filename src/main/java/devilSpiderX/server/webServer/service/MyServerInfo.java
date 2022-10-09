package devilSpiderX.server.webServer.service;

import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.service.information.CPU;
import devilSpiderX.server.webServer.service.information.Disk;
import devilSpiderX.server.webServer.service.information.Memory;
import devilSpiderX.server.webServer.service.information.Network;
import devilSpiderX.server.webServer.util.FormatUtil;
import io.vavr.Tuple2;
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
                .setA64bit(cp_pi.isCpu64bit());
        if (isTemperatureDetectable) {
            cpu.setTemperature(sensors.getCpuTemperature());
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

    public JSONObject constructCpuObject(CPU cpu) {
        JSONObject result = new JSONObject();
        result.put("name", cpu.getName());
        result.put("physicalNum", cpu.getPhysicalNum());
        result.put("logicalNum", cpu.getLogicalNum());
        result.put("usedRate", cpu.getUsedRate());
        result.put("is64bit", cpu.isA64bit());
        result.put("cpuTemperature", cpu.getTemperature());
        return result;
    }

    public JSONObject constructMemoryObject(Memory memory) {
        JSONObject result = new JSONObject();
        long total = memory.getTotal();
        long used = memory.getUsed();
        long free = memory.getFree();

        result.put("total", total);
        result.put("used", used);
        result.put("free", free);

        //格式化数据
        JSONObject format = new JSONObject();

        JSONObject totalFormat = new JSONObject();
        Tuple2<Double, String> totalUnit = FormatUtil.unitBytes(total, 2);
        totalFormat.put("value", totalUnit._1);
        totalFormat.put("unit", totalUnit._2);
        format.put("total", totalFormat);

        JSONObject usedFormat = new JSONObject();
        Tuple2<Double, String> usedUnit = FormatUtil.unitBytes(used, 2);
        usedFormat.put("value", usedUnit._1);
        usedFormat.put("unit", usedUnit._2);
        format.put("used", usedFormat);

        JSONObject freeFormat = new JSONObject();
        Tuple2<Double, String> freeUnit = FormatUtil.unitBytes(free, 2);
        freeFormat.put("value", freeUnit._1);
        freeFormat.put("unit", freeUnit._2);
        format.put("free", freeFormat);

        result.put("format", format);

        return result;
    }

    public JSONObject constructDiskObject(Disk disk) {
        JSONObject result = new JSONObject();
        long total = disk.getTotal();
        long free = disk.getFree();
        long used = disk.getUsed();

        result.put("label", disk.getLabel());
        result.put("mount", disk.getMount());
        result.put("fSType", disk.getFSType());
        result.put("name", disk.getName());
        result.put("total", total);
        result.put("free", free);
        result.put("used", used);

        //格式化数据
        JSONObject format = new JSONObject();

        JSONObject totalFormat = new JSONObject();
        Tuple2<Double, String> totalUnit = FormatUtil.unitBytes(total, 2);
        totalFormat.put("value", totalUnit._1);
        totalFormat.put("unit", totalUnit._2);
        format.put("total", totalFormat);

        JSONObject freeFormat = new JSONObject();
        Tuple2<Double, String> freeUnit = FormatUtil.unitBytes(free, 2);
        freeFormat.put("value", freeUnit._1);
        freeFormat.put("unit", freeUnit._2);
        format.put("free", freeFormat);

        JSONObject usedFormat = new JSONObject();
        Tuple2<Double, String> usedUnit = FormatUtil.unitBytes(used, 2);
        usedFormat.put("value", usedUnit._1);
        usedFormat.put("unit", usedUnit._2);
        format.put("used", usedFormat);

        result.put("format", format);

        return result;
    }

    public JSONObject constructNetworkObject(Network network) {
        JSONObject result = new JSONObject();
        long uploadSpeed = network.getUploadSpeed();
        long downloadSpeed = network.getDownloadSpeed();

        result.put("uploadSpeed", uploadSpeed);
        result.put("downloadSpeed", downloadSpeed);

        //格式化数据
        JSONObject format = new JSONObject();

        JSONObject uploadSpeedFormat = new JSONObject();
        Tuple2<Double, String> uploadSpeedUnit = FormatUtil.unitBytes(uploadSpeed, 2);
        uploadSpeedFormat.put("value", uploadSpeedUnit._1);
        uploadSpeedFormat.put("unit", uploadSpeedUnit._2 + "/s");
        format.put("uploadSpeed", uploadSpeedFormat);

        JSONObject downloadSpeedFormat = new JSONObject();
        Tuple2<Double, String> downloadSpeedUnit = FormatUtil.unitBytes(downloadSpeed, 2);
        downloadSpeedFormat.put("value", downloadSpeedUnit._1);
        downloadSpeedFormat.put("unit", downloadSpeedUnit._2 + "/s");
        format.put("downloadSpeed", downloadSpeedFormat);

        result.put("format", format);

        return result;
    }
}
