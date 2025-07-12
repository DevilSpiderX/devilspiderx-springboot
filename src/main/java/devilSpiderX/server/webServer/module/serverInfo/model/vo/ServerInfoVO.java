package devilSpiderX.server.webServer.module.serverInfo.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Schema(description = "系统信息Vo")
public record ServerInfoVO(
        @Nullable
        CPUVO cpu,
        @Nullable
        MemoryVO memory,
        @Nonnull
        List<DiskVO> disks,
        @Nonnull
        List<NetworkVO> networks,
        @Nullable
        CurrentOSVO os
) {
    public ServerInfoVO(
            @Nullable final CPUVO cpu,
            @Nullable final MemoryVO memory,
            @Nullable final List<DiskVO> disks,
            @Nullable final List<NetworkVO> networks,
            @Nullable final CurrentOSVO os
    ) {
        this.cpu = cpu;
        this.memory = memory;
        this.disks = Objects.isNull(disks) ? Collections.emptyList() : disks;
        this.networks = Objects.isNull(networks) ? Collections.emptyList() : networks;
        this.os = os;
    }
}
