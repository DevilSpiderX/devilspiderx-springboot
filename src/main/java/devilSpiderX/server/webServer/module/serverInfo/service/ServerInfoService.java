package devilSpiderX.server.webServer.module.serverInfo.service;

import devilSpiderX.server.webServer.module.serverInfo.statistic.*;
import devilSpiderX.server.webServer.module.serverInfo.vo.*;

public interface ServerInfoService {
    CPU getCPU();

    CPUVo constructCpuObject(CPU cpu);

    Memory getMemory();

    MemoryVo constructMemoryObject(Memory memory);

    Disk[] getDisks();

    DiskVo constructDiskObject(Disk disk);

    Network[] getNetworks();

    NetworkVo constructNetworkObject(Network network);

    CurrentOS getCurrentOS();

    CurrentOSVo constructCurrentOSObject(CurrentOS currentOS);
}
