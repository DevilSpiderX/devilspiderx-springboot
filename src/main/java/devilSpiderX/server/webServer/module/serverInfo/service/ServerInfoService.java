package devilSpiderX.server.webServer.module.serverInfo.service;

import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.module.serverInfo.statistics.*;

import java.util.List;

public interface ServerInfoService {
    CPU getCPU();

    JSONObject constructCpuObject(CPU cpu);

    Memory getMemory();

    JSONObject constructMemoryObject(Memory memory);

    List<Disk> getDisks();

    JSONObject constructDiskObject(Disk disk);

    List<Network> getNetworks();

    JSONObject constructNetworkObject(Network network);

    CurrentOS getCurrentOS();

    JSONObject constructCurrentOSObject(CurrentOS currentOS);
}
