package devilSpiderX.server.webServer.module.serverInfo.service;

import devilSpiderX.server.webServer.module.serverInfo.statistic.*;

import java.io.Serializable;
import java.util.Map;

public interface ServerInfoService {
    CPU getCPU();

    Map<String, Serializable> constructCpuObject(CPU cpu);

    Memory getMemory();

    Map<String, Serializable> constructMemoryObject(Memory memory);

    Disk[] getDisks();

    Map<String, Serializable> constructDiskObject(Disk disk);

    Network[] getNetworks();

    Map<String, Serializable> constructNetworkObject(Network network);

    CurrentOS getCurrentOS();

    Map<String, Serializable> constructCurrentOSObject(CurrentOS currentOS);
}
