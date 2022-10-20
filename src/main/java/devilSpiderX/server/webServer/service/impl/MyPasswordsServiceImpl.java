package devilSpiderX.server.webServer.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import devilSpiderX.server.webServer.entity.MyPasswords;
import devilSpiderX.server.webServer.service.MyPasswordsService;
import devilSpiderX.server.webServer.service.SettingsService;
import devilSpiderX.server.webServer.service.UserService;
import devilSpiderX.server.webServer.util.MyCipher;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.springframework.stereotype.Service;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;
import org.teasoft.honey.osql.core.ConditionImpl;

import javax.annotation.Resource;
import java.util.*;

@Service("myPasswordsService")
public class MyPasswordsServiceImpl implements MyPasswordsService {
    private final SuidRich suid = BeeFactoryHelper.getSuidRich();

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "settingsService")
    private SettingsService settingsService;

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
        if (suid.count(myPwd) > 0) {
            return false;
        }
        myPwd.setAccount(account);
        myPwd.setPassword(MyCipher.encrypt(password));
        myPwd.setRemark(remark);
        return suid.insert(myPwd, IncludeType.INCLUDE_EMPTY) == 1;
    }

    @Override
    public boolean delete(int id) {
        return suid.deleteById(MyPasswords.class, id) == 1;
    }

    @Override
    public boolean update(int id, String name, String account, String password, String remark) {
        if ("".equals(name)) {
            return false;
        }
        MyPasswords oldMyPwd = suid.selectById(new MyPasswords(), id);

        MyPasswords myPwd = new MyPasswords();
        myPwd.setName(name);
        myPwd.setOwner(oldMyPwd.getOwner());
        if (!oldMyPwd.getName().equals(name) && suid.count(myPwd) > 0) {
            return false;
        }
        myPwd.setId(id);
        myPwd.setAccount(account);
        myPwd.setPassword(MyCipher.encrypt(password));
        myPwd.setRemark(remark);
        return suid.updateById(myPwd, new ConditionImpl().setIncludeType(IncludeType.INCLUDE_EMPTY)) == 1;
    }

    @Override
    public JSONArray query(String name, String owner) {
        if (name == null) return query((List<String>) null, owner);
        return query(List.of(name), owner);
    }

    @Override
    public JSONArray query(String[] names, String owner) {
        if (names == null) return query((List<String>) null, owner);
        return query(List.of(names), owner);
    }

    @Override
    public JSONArray query(List<String> names, String owner) {
        JSONArray result = new JSONArray();
        List<MyPasswords> passwords;
        MyPasswords emptyMP = new MyPasswords();
        if (names == null || names.isEmpty() || (names.size() == 1 && names.get(0).equals(""))) {
            emptyMP.setOwner(owner);
            passwords = suid.select(emptyMP);
        } else {
            passwords = new ArrayList<>();
            Set<String> nameSet = new HashSet<>(names);
            List<String> nameList = new LinkedList<>(nameSet);
            nameList.remove("");

            for (String name : nameList) {
                Condition con = new ConditionImpl();
                con.op("name", Op.like, name + '%').and().op("owner", Op.equal, owner);
                List<MyPasswords> selectList = suid.select(emptyMP, con);
                if (selectList.isEmpty()) {
                    Condition con1 = new ConditionImpl();
                    con1.op("name", Op.like, '%' + name + '%').and().op("owner", Op.equal, owner);
                    selectList.addAll(suid.select(emptyMP, con1));
                    if (selectList.isEmpty()) {
                        StringBuilder nameKey = new StringBuilder("%");
                        for (char c : name.toCharArray()) {
                            nameKey.append(c).append('%');
                        }
                        Condition con2 = new ConditionImpl();
                        con2.op("name", Op.like, nameKey.toString()).and().op("owner", Op.equal, owner);
                        selectList.addAll(suid.select(emptyMP, con2));
                    }
                }
                passwords.addAll(selectList);
            }
        }
        passwords.sort(Comparator.naturalOrder());
        int lastId = -1;
        for (MyPasswords password : passwords) {
            int id = password.getId();
            putting:
            {
                if (id == lastId) break putting;
                JSONObject one = new JSONObject();
                one.put("id", id);
                one.put("name", password.getName());
                one.put("account", password.getAccount());
                one.put("password", MyCipher.decrypt(password.getPassword()));
                one.put("remark", password.getRemark());
                result.add(one);
            }
            lastId = id;
        }
        return result;
    }

    @Override
    public Tuple2<JSONArray, Integer> query(String name, int page, String owner) {
        JSONArray array = query(name, owner);
        return _query(array, page);
    }

    @Override
    public Tuple2<JSONArray, Integer> query(String[] names, int page, String owner) {
        JSONArray array = query(names, owner);
        return _query(array, page);
    }

    @Override
    public Tuple2<JSONArray, Integer> query(List<String> names, int page, String owner) {
        JSONArray array = query(names, owner);
        return _query(array, page);
    }

    private Tuple2<JSONArray, Integer> _query(JSONArray array, int page) {
        int pageSize = Integer.parseInt(settingsService.get("page_size"));
        int fromIndex = (page - 1) * pageSize;
        int toIndex = fromIndex + pageSize;
        int arraySize = array.size();
        if (fromIndex < 0) fromIndex = 0;
        if (fromIndex > arraySize) fromIndex = arraySize - pageSize;
        if (toIndex < 0) toIndex = pageSize;
        if (toIndex > arraySize) toIndex = arraySize;
        JSONArray t_1 = new JSONArray(array.subList(fromIndex, toIndex));
        int pageCount = array.size() / pageSize;
        if (array.size() % pageSize > 0) pageCount++;
        return Tuple.of(t_1, pageCount);
    }
}
