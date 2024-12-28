package devilSpiderX.server.webServer.module.serverInfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;

@Schema(description = "网络信息Vo")
public record NetworkVo(
        @Schema(description = "网卡名")
        @Nonnull
        String name,
        @Schema(description = "网络名")
        @Nonnull
        String displayName,
        @Schema(description = "MAC地址")
        @Nonnull
        String macAddr,
        @Schema(description = "发送的字节数")
        long bytesSent,
        @Schema(description = "接收的字节数")
        long bytesRecv,
        @Schema(description = "上传速度")
        long uploadSpeed,
        @Schema(description = "下载速度")
        long downloadSpeed,
        @Schema(description = "ipv4地址")
        @Nonnull
        String[] IPv4addr,
        @Schema(description = "ipv6地址")
        @Nonnull
        String[] IPv6addr
) {
}
