package devilSpiderX.server.webServer.core.vo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CommonPage<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = -5421754385962369237L;
    private List<T> list;//数据
    private long total;//数据总条数
    private long page;//当前页
    private long pageSize;//每页的条数

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
