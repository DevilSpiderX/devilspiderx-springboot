package devilSpiderX.server.webServer.sql;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.IncludeType;
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
        if (name == null || name.equals("")) {
            return false;
        }
        if (!User.exist(owner)) {
            return false;
        }
        SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();
        MyPasswords counter = new MyPasswords();
        counter.setName(name);
        counter.setOwner(owner);
        if (suidRich.count(counter) > 0) {
            return false;
        }
        return suidRich.insert(this) == 1;
    }

    public boolean delete() {
        return delete(id);
    }

    public static boolean delete(int id) {
        SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();
        return suidRich.deleteById(MyPasswords.class, id) == 1;
    }

    public boolean update() {
        if ("".equals(name)) {
            return false;
        }
        if (!User.exist(owner)) {
            return false;
        }
        SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();
        return suidRich.updateById(this, new ConditionImpl().setIncludeType(IncludeType.INCLUDE_EMPTY)) == 1;
    }

    public static boolean update(int id, String name, String account, String password, String remark, String owner) {
        MyPasswords passwords = new MyPasswords();
        passwords.setId(id);
        passwords.setName(name);
        passwords.setAccount(account);
        passwords.setPassword(password);
        passwords.setRemark(remark);
        passwords.setOwner(owner);
        return passwords.update();
    }

    public JSONArray query() {
        return query(name, owner);
    }

    public static JSONArray query(String name, String owner) {
        JSONArray result = new JSONArray();
        SuidRich suidRich = BeeFactory.getHoneyFactory().getSuidRich();
        Condition con = new ConditionImpl();
        con.op("name", Op.like, name + '%').and().op("owner", Op.equal, owner);
        MyPasswords emptyMP = new MyPasswords();
        List<MyPasswords> passwords = suidRich.select(emptyMP, con);
        if (passwords.isEmpty()) {
            Condition con1 = new ConditionImpl();
            con1.op("name", Op.like, '%' + name + '%').and().op("owner", Op.equal, owner);
            passwords.addAll(suidRich.select(emptyMP, con1));
            if (passwords.isEmpty()) {
                StringBuilder nameKey = new StringBuilder("%");
                for (char c : name.toCharArray()) {
                    nameKey.append(c).append('%');
                }
                Condition con2 = new ConditionImpl();
                con2.op("name", Op.like, nameKey.toString()).and().op("owner", Op.equal, owner);
                passwords.addAll(suidRich.select(emptyMP, con2));
            }
        }
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