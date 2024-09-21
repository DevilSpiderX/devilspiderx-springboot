package devilSpiderX.server.webServer.module.query.dto;

/**
 * 查询密码记录请求参数
 *
 * @param key 查询值
 */
public record GetRequestDto(String key) {
    /**
     * 分割查询值,使用空格和<code>.</code>来分割
     *
     * @return 分割后的查询值
     */
    public String[] keys() {
        if (key != null) {
            final var keysStr = key.trim();
            return keysStr.split("(\\s|\\.)+");
        }
        return new String[0];
    }
}
