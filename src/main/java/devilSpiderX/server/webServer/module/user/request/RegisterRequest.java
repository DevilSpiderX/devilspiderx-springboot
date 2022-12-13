package devilSpiderX.server.webServer.module.user.request;

public class RegisterRequest {
    private String uid;
    private String pwd;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "uid='" + uid + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}
