package devilSpiderX.server.webServer.module.query.service.impl;

import devilSpiderX.server.webServer.core.entity.MyPasswords;
import devilSpiderX.server.webServer.core.service.SettingsService;
import devilSpiderX.server.webServer.core.util.MyCipher;
import devilSpiderX.server.webServer.module.query.service.MyPasswordsService;
import devilSpiderX.server.webServer.module.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.teasoft.bee.osql.Condition;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.io.Serializable;
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
    public List<Map<String, Serializable>> query(String name, String owner) {
        if (name == null) return _query(null, owner);
        return _query(List.of(name), owner);
    }

    @Override
    public List<Map<String, Serializable>> query(String[] names, String owner) {
        if (names == null) return _query(null, owner);
        return _query(List.of(names), owner);
    }

    @Override
    public List<Map<String, Serializable>> query(List<String> names, String owner) {
        return _query(names, owner);
    }

    private List<Map<String, Serializable>> _query(List<String> names, String owner) {
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

        List<Map<String, Serializable>> result = new ArrayList<>();
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
    public pageQueryRecord query(String name, int page, String owner) {
        List<Map<String, Serializable>> array = query(name, owner);
        return subQueryResult(array, page);
    }

    @Override
    public pageQueryRecord query(String[] names, int page, String owner) {
        List<Map<String, Serializable>> array = query(names, owner);
        return subQueryResult(array, page);
    }

    @Override
    public pageQueryRecord query(List<String> names, int page, String owner) {
        List<Map<String, Serializable>> array = query(names, owner);
        return subQueryResult(array, page);
    }

    private pageQueryRecord subQueryResult(List<Map<String, Serializable>> array, int page) {
        int pageSize = Integer.parseInt(settingsService.get("page_size"));
        int fromIndex = (page - 1) * pageSize;
        int toIndex = fromIndex + pageSize;
        int arraySize = array.size();

        if (fromIndex < 0)
            fromIndex = 0;
        if (fromIndex > arraySize)
            fromIndex = arraySize - pageSize;
        if (toIndex < 0)
            toIndex = pageSize;
        if (toIndex > arraySize)
            toIndex = arraySize;

        List<Map<String, Serializable>> sub = array.subList(fromIndex, toIndex);
        int pageCount = array.size() / pageSize;
        if (array.size() % pageSize > 0) pageCount++;
        return new pageQueryRecord(sub, pageCount);
    }
}
