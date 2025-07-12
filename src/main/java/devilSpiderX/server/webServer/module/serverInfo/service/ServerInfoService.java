package devilSpiderX.server.webServer.module.serverInfo.service;

import devilSpiderX.server.webServer.module.serverInfo.model.vo.*;
import devilSpiderX.server.webServer.module.serverInfo.statistic.*;

public interface ServerInfoService {
    CPU getCPU();

    CPUVO constructCpuObject(CPU cpu);

    Memory getMemory();

    MemoryVO constructMemoryObject(Memory memory);

    Disk[] getDisks();

    DiskVO constructDiskObject(Disk disk);

    Network[] getNetworks();

    NetworkVO constructNetworkObject(Network network);

    CurrentOS getCurrentOS();

    CurrentOSVO constructCurrentOSObject(CurrentOS currentOS);

    ServerInfoVO getServerInfo();
}
