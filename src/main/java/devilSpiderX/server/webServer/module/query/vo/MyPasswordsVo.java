package devilSpiderX.server.webServer.module.query.vo;

public record MyPasswordsVo(
        int id,
        String name,
        String account,
        String password,
        String remark
) {
}