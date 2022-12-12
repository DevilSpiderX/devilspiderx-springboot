package devilSpiderX.server.webServer.service.impl;

import com.alibaba.fastjson2.JSONArray;
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
        myPwd.setDeleted(false);
        return suid.insert(myPwd, IncludeType.INCLUDE_EMPTY) == 1;
    }

    @Override
    public boolean delete(int id) {
        MyPasswords deletingPwd = new MyPasswords();
        deletingPwd.setId(id);
        deletingPwd.setDeleted(true);
        return suid.updateById(deletingPwd, new ConditionImpl().setIncludeType(IncludeType.EXCLUDE_BOTH)) == 1;
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
        if (name == null) return _query(null, owner);
        return _query(List.of(name), owner);
    }

    @Override
    public JSONArray query(String[] names, String owner) {
        if (names == null) return _query(null, owner);
        return _query(List.of(names), owner);
    }

    @Override
    public JSONArray query(List<String> names, String owner) {
        return _query(names, owner);
    }

    private JSONArray _query(List<String> names, String owner) {
        List<MyPasswords> passwords;
        MyPasswords emptyMP = new MyPasswords();
        if (names == null || names.isEmpty() || (names.size() == 1 && names.get(0).equals(""))) {
            emptyMP.setOwner(owner);
            passwords = suid.select(emptyMP);
        } else {
            Set<String> nameSet = new HashSet<>(names);
            List<String> nameList = new LinkedList<>(nameSet);
            nameList.remove("");

            Condition con = new ConditionImpl();
            con.lParentheses();
            for (int i = 0; i < nameList.size(); i++) {
                String name = nameList.get(i);
                if (i == 0) {
                    con.op("name", Op.like, '%' + name + '%');
                    continue;
                }
                con.or().op("name", Op.like, '%' + name + '%');
            }
            con.rParentheses().and().op("owner", Op.equal, owner);
            passwords = suid.select(emptyMP, con);
        }

        List<MyPasswords> deletedPasswords = new ArrayList<>();
        for (MyPasswords password : passwords) {
            if (password.getDeleted()) {
                deletedPasswords.add(password);
            }
        }
        passwords.removeAll(deletedPasswords);

        passwords.sort(Comparator.naturalOrder());

        JSONArray result = new JSONArray();
        for (MyPasswords password : passwords) {
            result.add(Map.of(
                    "id", password.getId(),
                    "name", password.getName(),
                    "account", password.getAccount(),
                    "password", MyCipher.decrypt(password.getPassword()),
                    "remark", password.getRemark()
            ));
        }
        return result;
    }

    @Override
    public Tuple2<JSONArray, Integer> query(String name, int page, String owner) {
        JSONArray array = query(name, owner);
        return subQueryResult(array, page);
    }

    @Override
    public Tuple2<JSONArray, Integer> query(String[] names, int page, String owner) {
        JSONArray array = query(names, owner);
        return subQueryResult(array, page);
    }

    @Override
    public Tuple2<JSONArray, Integer> query(List<String> names, int page, String owner) {
        JSONArray array = query(names, owner);
        return subQueryResult(array, page);
    }

    private Tuple2<JSONArray, Integer> subQueryResult(JSONArray array, int page) {
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
