package devilSpiderX.server.webServer.sql;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactory;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class MyPasswords implements Serializable, Comparable<MyPasswords> {

    private static final long serialVersionUID = 1594978966205L;

    private Integer id;
    private String name;
    private String account;
    private String password;
    private String remark;
    private String owner;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "MyPasswords{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", remark='" + remark + '\'' +
                ", owner='" + owner + '\'' +
                '}';
    }

    public boolean add() {
        SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();
        MyPasswords counter = new MyPasswords();
        counter.setName(name);
        counter.setOwner(owner);
        if (suidRich.count(counter) > 0) {
            return false;
        }
        return suidRich.insert(this) == 1;
    }

    public JSONArray getMyPasswords(String name) {
        return getMyPasswords(name, owner);
    }

    public static JSONArray getMyPasswords(String name, String owner) {
        JSONArray result = new JSONArray();
        SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();
        StringBuilder nameKey = new StringBuilder("%");
        for (char c : name.toCharArray()) {
            nameKey.append(c).append('%');
        }
        Condition con = new ConditionImpl();
        con.op("name", Op.like, "%" + nameKey + "%").and().op("owner", Op.equal, owner);
        List<MyPasswords> passwords = suidRich.select(new MyPasswords(), con);
        passwords.sort(Comparator.naturalOrder());
        for (MyPasswords password : passwords) {
            JSONObject one = new JSONObject();
            one.put("id", password.getId());
            one.put("name", password.getName());
            one.put("account", password.getAccount());
            one.put("password", password.getPassword());
            one.put("remark", password.getRemark());
            result.add(one);
        }
        return result;
    }

    @Override
    public int compareTo(MyPasswords another) {
        return id.compareTo(another.id);
    }
}