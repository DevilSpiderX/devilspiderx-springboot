package devilSpiderX.server.webServer.module.query.service;

import devilSpiderX.server.webServer.core.resp.CommonPage;
import devilSpiderX.server.webServer.module.query.model.dto.AddRequestDTO;
import devilSpiderX.server.webServer.module.query.model.dto.UpdateRequestDTO;
import devilSpiderX.server.webServer.module.query.model.vo.MyPasswordsVO;
import jakarta.annotation.Nonnull;

import java.util.List;

public interface MyPasswordsService {

    void add(@Nonnull AddRequestDTO dto);

    void delete(int id);

    void update(int id, @Nonnull UpdateRequestDTO dto);

    default @Nonnull List<MyPasswordsVO> query(String name) {
        if (name == null) return query((List<String>) null);
        return query(List.of(name));
    }

    default @Nonnull List<MyPasswordsVO> query(String[] names) {
        if (names == null) return query((List<String>) null);
        return query(List.of(names));
    }

    @Nonnull
    List<MyPasswordsVO> query(List<String> names);

    default @Nonnull CommonPage<MyPasswordsVO> queryPaging(String name, int current, int pageSize) {
        if (name == null) return queryPaging((List<String>) null, current, pageSize);
        return queryPaging(List.of(name), current, pageSize);
    }

    default @Nonnull CommonPage<MyPasswordsVO> queryPaging(String[] names, int current, int pageSize) {
        if (names == null) return queryPaging((List<String>) null, current, pageSize);
        return queryPaging(List.of(names), current, pageSize);
    }

    @Nonnull
    CommonPage<MyPasswordsVO> queryPaging(
            List<String> names,
            int current,
            int pageSize
    );
}
