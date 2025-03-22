package devilSpiderX.server.webServer.module.serverInfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Schema(description = "系统信息Vo")
public record ServerInfoVo(
        @Nullable
        CPUVo cpu,
        @Nullable
        MemoryVo memory,
        @Nonnull
        List<DiskVo> disks,
        @Nonnull
        List<NetworkVo> networks,
        @Nullable
        CurrentOSVo os
) {
    public ServerInfoVo(
            @Nullable final CPUVo cpu,
            @Nullable final MemoryVo memory,
            @Nullable final List<DiskVo> disks,
            @Nullable final List<NetworkVo> networks,
            @Nullable final CurrentOSVo os
    ) {
        this.cpu = cpu;
        this.memory = memory;
        this.disks = Objects.isNull(disks) ? Collections.emptyList() : disks;
        this.networks = Objects.isNull(networks) ? Collections.emptyList() : networks;
        this.os = os;
    }
}
