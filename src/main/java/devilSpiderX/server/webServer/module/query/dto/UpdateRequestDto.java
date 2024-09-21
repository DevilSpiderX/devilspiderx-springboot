package devilSpiderX.server.webServer.module.query.dto;

/**
 * 修改密码记录请求参数
 *
 * @param id       记录id
 * @param name     名称(可选)
 * @param account  账号(可选)
 * @param password 密码(可选)
 * @param remark   备注(可选)
 */
public record UpdateRequestDto(Integer id, String name, String account, String password, String remark) {
}
