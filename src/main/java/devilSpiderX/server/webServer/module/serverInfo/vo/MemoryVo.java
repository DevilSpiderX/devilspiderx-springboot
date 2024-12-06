package devilSpiderX.server.webServer.module.serverInfo.vo;

public record MemoryVo(
        long total,
        long used,
        long free
) {
}
