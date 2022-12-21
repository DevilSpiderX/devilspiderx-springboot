package devilSpiderX.server.webServer.module.serverInfo.service;

import devilSpiderX.server.webServer.module.serverInfo.statistic.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface ServerInfoService {
    CPU getCPU();

    Map<String, Serializable> constructCpuObject(CPU cpu);

    Memory getMemory();

    Map<String, Serializable> constructMemoryObject(Memory memory);

    List<Disk> getDisks();

    Map<String, Serializable> constructDiskObject(Disk disk);

    List<Network> getNetworks();

    Map<String, Serializable> constructNetworkObject(Network network);

    CurrentOS getCurrentOS();

    Map<String, Serializable> constructCurrentOSObject(CurrentOS currentOS);
}
