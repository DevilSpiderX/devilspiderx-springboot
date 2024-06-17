package devilSpiderX.server.webServer.module.query.record;

import java.util.List;

/**
 * 分页查询返回的数据结构
 *
 * @param list      包含查询数据的列表，长度小于等于分页的长度
 * @param dataCount 可查询到的所有数据的个数
 */
public record MyPasswordPagingResp(
        List<MyPasswordsResp> list,
        long dataCount
) {
}
