package devilSpiderX.server.webServer.module.query.record;

public record MyPasswordsResp(
        int id,
        String name,
        String account,
        String password,
        String remark
) {
}
