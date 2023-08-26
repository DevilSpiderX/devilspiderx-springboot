package devilSpiderX.server.webServer.module.query.service.impl;

import devilSpiderX.server.webServer.core.util.MyCipher;
import devilSpiderX.server.webServer.module.query.entity.MyPasswords;
import devilSpiderX.server.webServer.module.query.record.MyPasswordsResp;
import devilSpiderX.server.webServer.module.query.service.MyPasswordsService;
import devilSpiderX.server.webServer.module.user.service.UserService;
import org.springframework.stereotype.Service;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.api.Condition;
import org.teasoft.bee.osql.api.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.util.*;

@Service
public class MyPasswordsServiceImpl implements MyPasswordsService {
    private final SuidRich suid = BeeFactoryHelper.getSuidRich();
    private final UserService userService;

    public MyPasswordsServiceImpl(UserService userService) {
        this.userService = userService;
    }

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
        MyPasswords oldMyPwd = suid.selectById(MyPasswords.class, id);

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
    public List<MyPasswordsResp> query(String name, String owner) {
        if (name == null) return _query(null, owner);
        return _query(List.of(name), owner);
    }

    @Override
    public List<MyPasswordsResp> query(String[] names, String owner) {
        if (names == null) return _query(null, owner);
        return _query(List.of(names), owner);
    }

    @Override
    public List<MyPasswordsResp> query(List<String> names, String owner) {
        return _query(names, owner);
    }

    private boolean isEmptyNames(List<String> names) {
        return names == null || names.isEmpty() || (names.size() == 1 && names.get(0).equals(""));
    }

    private List<MyPasswordsResp> _query(List<String> names, String owner) {
        List<MyPasswords> passwords;
        MyPasswords emptyMP = new MyPasswords();
        if (isEmptyNames(names)) {
            emptyMP.setOwner(owner);
            passwords = suid.select(emptyMP);
        } else {
            final Set<String> nameSet = new HashSet<>(names);
            final List<String> nameList = nameSet.stream().filter(name -> !Objects.equals(name, "")).toList();

            Condition con = new ConditionImpl();
            con.lParentheses();
            for (int i = 0; i < nameList.size(); i++) {
                final String name = nameList.get(i);
                if (i != 0) con.or();
                con.op("name", Op.like, '%' + name + '%');
            }
            con.rParentheses().and().op("owner", Op.equal, owner);
            passwords = suid.select(emptyMP, con);
        }
        passwords.sort(Comparator.naturalOrder());

        passwords = passwords.stream().filter(password -> !password.getDeleted()).toList();

        List<MyPasswordsResp> result = new ArrayList<>();
        for (MyPasswords password : passwords) {
            result.add(new MyPasswordsResp(
                    password.getId(),
                    password.getName(),
                    password.getAccount(),
                    MyCipher.decrypt(password.getPassword()),
                    password.getRemark()
            ));
        }
        return result;
    }
}
