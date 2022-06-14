package devilSpiderX.server.webServer.sql;

import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.BeeFactoryHelper;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private static final long serialVersionUID = 1595929511542L;

    private String uid;
    private String password;
    private Boolean admin;

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

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public boolean exist() {
        if (uid == null) {
            return false;
        }
        SuidRich suidRich = BeeFactoryHelper.getSuidRich();
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
                ", admin=" + admin +
                '}';
    }

    public static boolean isAdmin(String uid) {
        User user = BeeFactory.getHoneyFactory().getSuidRich().select(new User(uid)).get(0);
        return user != null && user.getAdmin();
    }

    public static void main(String[] args) {
        SuidRich suidRich = BeeFactoryHelper.getSuidRich();
        List<User> userList = suidRich.select(new User());
        System.out.println(userList);
    }
}