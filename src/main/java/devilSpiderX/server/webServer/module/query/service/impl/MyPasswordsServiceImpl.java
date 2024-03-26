package devilSpiderX.server.webServer.module.query.service.impl;

import devilSpiderX.server.webServer.core.util.MyCipher;
import devilSpiderX.server.webServer.module.query.entity.MyPasswords;
import devilSpiderX.server.webServer.module.query.entity.MyPasswordsDeleted;
import devilSpiderX.server.webServer.module.query.record.MyPasswordsResp;
import devilSpiderX.server.webServer.module.query.service.MyPasswordsService;
import devilSpiderX.server.webServer.module.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.teasoft.bee.osql.IncludeType;
import org.teasoft.bee.osql.Op;
import org.teasoft.bee.osql.api.SuidRich;
import org.teasoft.honey.osql.core.BeeFactoryHelper;
import org.teasoft.honey.osql.core.ConditionImpl;

import java.util.*;

@Service
public class MyPasswordsServiceImpl implements MyPasswordsService {
    private static final Logger logger = LoggerFactory.getLogger(MyPasswordsServiceImpl.class);
    private final SuidRich suid = BeeFactoryHelper.getSuidRich();
    private final UserService userService;

    public MyPasswordsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean add(String name, String account, String password, String remark, String owner) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        if (!userService.exist(owner)) {
            return false;
        }
        MyPasswords myPwd = new MyPasswords();
        myPwd.setName(name);
        myPwd.setOwner(owner);
        if (suid.count(myPwd) > 0) {
            logger.error("my_password表中已存在name为{}的行", name);
            return false;
        }
        myPwd.setAccount(account);
        myPwd.setPassword(MyCipher.encrypt(password));
        myPwd.setRemark(remark);
        return suid.insert(myPwd, IncludeType.INCLUDE_EMPTY) == 1;
    }

    @Override
    public boolean delete(int id) {
        final MyPasswordsDeleted deletedEntity = new MyPasswordsDeleted(
                suid.selectById(MyPasswords.class, id)
        );

        int flag = 0;
        flag += suid.insert(deletedEntity);
        if (flag != 1) {
            logger.error("my_password_deleted表插入失败,id:{}", id);
            return false;
        }

        flag += suid.deleteById(MyPasswords.class, id);
        if (flag != 2) logger.error("my_password表删除失败,id:{}", id);
        return flag == 2;
    }

    @Override
    public boolean update(int id, String name, String account, String password, String remark) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        final var oldMyPwd = suid.selectById(MyPasswords.class, id);

        final MyPasswords myPwd = new MyPasswords();
        myPwd.setName(name);
        myPwd.setOwner(oldMyPwd.getOwner());
        if (!oldMyPwd.getName().equals(name) && suid.count(myPwd) > 0) {
            logger.error("my_password表中已存在name为{}的行", name);
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
        return names == null || names.isEmpty() || (names.size() == 1 && names.getFirst().isEmpty());
    }

    private List<MyPasswordsResp> _query(List<String> names, String owner) {
        List<MyPasswords> passwords;
        final var emptyMP = new MyPasswords();
        if (isEmptyNames(names)) {
            emptyMP.setOwner(owner);
            passwords = suid.select(emptyMP);
        } else {
            final Set<String> nameSet = new HashSet<>(names);
            final List<String> nameList = nameSet.stream().filter(name -> !Objects.equals(name, "")).toList();

            final var con = new ConditionImpl();
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

        final List<MyPasswordsResp> result = new ArrayList<>();
        for (var password : passwords) {
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
