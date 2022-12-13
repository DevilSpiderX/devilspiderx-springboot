package devilSpiderX.server.webServer.module.serverInfo.service.impl;

import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.module.serverInfo.service.ServerInfoService;
import devilSpiderX.server.webServer.module.serverInfo.statistics.*;
import devilSpiderX.server.webServer.core.util.FormatUtil;
import io.vavr.Tuple2;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service("serverInfoService")
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public JSONObject constructCurrentOSObject(CurrentOS currentOS) {
        JSONObject result = new JSONObject();
        result.put("name", currentOS.getName());
        result.put("bitness", currentOS.getBitness());
        result.put("processCount", currentOS.getProcessCount());
        return result;
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
