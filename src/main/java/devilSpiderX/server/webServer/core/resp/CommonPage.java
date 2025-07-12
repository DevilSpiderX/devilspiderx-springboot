package devilSpiderX.server.webServer.core.resp;

import com.mybatisflex.core.paginate.Page;
import devilSpiderX.server.webServer.core.constant.PageConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author DevilSpiderX
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用分页类型")
public class CommonPage<T> implements Serializable {

    @Schema(description = "数据")
    private @Nonnull List<T> records = Collections.emptyList();
    @Schema(description = "当前页码,第一页为：1")
    private long current = PageConstant.DEFAULT_CURRENT;
    @Schema(description = "每页数据数量")
    private long pageSize = PageConstant.DEFAULT_PAGE_SIZE;
    @Schema(description = "总数据数量")
    private long total = 0;

    public CommonPage(final @Nonnull Page<T> page) {
        this(page.getRecords(), page.getPageNumber(), page.getPageSize(), page.getTotalRow());
    }

    public <S> CommonPage(final @Nonnull Page<S> page, final @Nonnull Function<List<S>, List<T>> converter) {
        this(converter.apply(page.getRecords()), page.getPageNumber(), page.getPageSize(), page.getTotalRow());
    }

    public CommonPage(final @Nonnull List<T> list) {
        this(list, 1, Math.max(list.size(), PageConstant.DEFAULT_PAGE_SIZE), list.size());
    }

}


