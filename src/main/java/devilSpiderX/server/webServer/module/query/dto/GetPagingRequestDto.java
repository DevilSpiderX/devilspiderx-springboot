package devilSpiderX.server.webServer.module.query.dto;

/**
 * 分页查询密码记录请求参数
 *
 * @param key    查询值
 * @param length 每页的长度
 * @param page   查询第n页
 */
public record GetPagingRequestDto(String key, Integer length, Integer page) {
    public GetPagingRequestDto(String key, Integer length, Integer page) {
        this.key = key;
        this.length = length == null ? 20 : length;
        this.page = page == null ? 0 : page;
    }

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
