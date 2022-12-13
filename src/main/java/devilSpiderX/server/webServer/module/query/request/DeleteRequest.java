package devilSpiderX.server.webServer.module.query.request;

public class DeleteRequest {
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "DeleteRequest{" +
                "id=" + id +
                '}';
    }
}
