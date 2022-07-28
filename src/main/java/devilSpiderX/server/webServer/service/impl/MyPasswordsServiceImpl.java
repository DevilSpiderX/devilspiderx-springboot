package devilSpiderX.server.webServer.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.entity.MyPasswords;
import devilSpiderX.server.webServer.util.MyCipher;
import devilSpiderX.server.webServer.service.MyPasswordsService;
import devilSpiderX.server.webServer.service.UserService;
import org.springframework.stereotype.Service;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;
import org.teasoft.honey.osql.core.ConditionImpl;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

@Service("myPasswordsService")
public class MyPasswordsServiceImpl implements MyPasswordsService {
    private final SuidRich dao = BeeFactoryHelper.getSuidRich();

    @Resource(name = "userService")
    private UserService userService;

    @Override
    public boolean add(String name, String account, String password, String remark, String owner) {
        if (name == null || name.equals("")) {
            return false;
        }
        if (!userService.exist(owner)) {
            return false;
        }
        MyPasswords myPwd = new MyPasswords();
        myPwd.setName(name);
        myPwd.setOwner(owner);
        if (dao.count(myPwd) > 0) {
            return false;
        }
        myPwd.setAccount(account);
        myPwd.setPassword(MyCipher.encrypt(password));
        myPwd.setRemark(remark);
        return dao.insert(myPwd, IncludeType.INCLUDE_EMPTY) == 1;
    }

    @Override
    public boolean delete(int id) {
        return dao.deleteById(MyPasswords.class, id) == 1;
    }

    @Override
    public boolean update(int id, String name, String account, String password, String remark) {
        if ("".equals(name)) {
            return false;
        }
        MyPasswords oldMyPwd = dao.selectById(new MyPasswords(), id);

        MyPasswords myPwd = new MyPasswords();
        myPwd.setName(name);
        myPwd.setOwner(oldMyPwd.getOwner());
        if (!oldMyPwd.getName().equals(name) && dao.count(myPwd) > 0) {
            return false;
        }
        myPwd.setId(id);
        myPwd.setAccount(account);
        myPwd.setPassword(MyCipher.encrypt(password));
        myPwd.setRemark(remark);
        return dao.updateById(myPwd, new ConditionImpl().setIncludeType(IncludeType.INCLUDE_EMPTY)) == 1;
    }

    @Override
    public JSONArray query(String name, String owner) {
        JSONArray result = new JSONArray();
        Condition con = new ConditionImpl();
        con.op("name", Op.like, name + '%').and().op("owner", Op.equal, owner);
        MyPasswords emptyMP = new MyPasswords();
        List<MyPasswords> passwords = dao.select(emptyMP, con);
        if (passwords.isEmpty()) {
            Condition con1 = new ConditionImpl();
            con1.op("name", Op.like, '%' + name + '%').and().op("owner", Op.equal, owner);
            passwords.addAll(dao.select(emptyMP, con1));
            if (passwords.isEmpty()) {
                StringBuilder nameKey = new StringBuilder("%");
                for (char c : name.toCharArray()) {
                    nameKey.append(c).append('%');
                }
                Condition con2 = new ConditionImpl();
                con2.op("name", Op.like, nameKey.toString()).and().op("owner", Op.equal, owner);
                passwords.addAll(dao.select(emptyMP, con2));
            }
        }
        passwords.sort(Comparator.naturalOrder());
        for (MyPasswords password : passwords) {
            JSONObject one = new JSONObject();
            one.put("id", password.getId());
            one.put("name", password.getName());
            one.put("account", password.getAccount());
            one.put("password", MyCipher.decrypt(password.getPassword()));
            one.put("remark", password.getRemark());
            result.add(one);
        }
        return result;
    }
}
