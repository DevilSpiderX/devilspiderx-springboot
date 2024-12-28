package devilSpiderX.server.webServer.core.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Schema(description = "通用分页类型")
public class CommonPage<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -5421754385962369237L;

    @Schema(description = "数据")
    private List<T> list;
    @Schema(description = "数据总条数")
    private long total;
    @Schema(description = "当前页")
    private long page;
    @Schema(description = "每页的条数")
    private long pageSize;

    public CommonPage() {
    }

    public CommonPage(List<T> list, long total, long page, long pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "CommonPage{" +
                "list=" + list +
                ", total=" + total +
                ", page=" + page +
                ", pageSize=" + pageSize +
                '}';
    }
}
