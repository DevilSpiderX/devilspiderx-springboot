package devilSpiderX.server.webServer.sql;

import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactory;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 1595929511542L;

    private String uid;
    private String password;

    public User() {
    }

    public User(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean exist() {
        if (uid == null) {
            return false;
        }
        SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();
        return suidRich.exist(this);
    }

    public static boolean exist(String uid) {
        return new User(uid).exist();
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}