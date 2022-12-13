package devilSpiderX.server.webServer.module.query.request;

public class GetRequest {
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "GetRequest{" +
                "key='" + key + '\'' +
                '}';
    }
}
