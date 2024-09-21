package devilSpiderX.server.webServer.module.query.dto;

/**
 * 添加密码记录请求参数
 *
 * @param name     名称
 * @param account  账号
 * @param password 密码
 * @param remark   备注
 */
public record AddRequestDto(String name, String account, String password, String remark) {
}
