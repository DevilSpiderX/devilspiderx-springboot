package devilSpiderX.server.webServer.module.query.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import devilSpiderX.server.webServer.core.exception.BaseException;
import devilSpiderX.server.webServer.core.util.AjaxCode;
import devilSpiderX.server.webServer.core.util.MyCipher;
import devilSpiderX.server.webServer.core.vo.CommonPage;
import devilSpiderX.server.webServer.module.query.dao.MyPasswordsDeletedMapper;
import devilSpiderX.server.webServer.module.query.dao.MyPasswordsMapper;
import devilSpiderX.server.webServer.module.query.entity.MyPasswords;
import devilSpiderX.server.webServer.module.query.entity.MyPasswordsDeleted;
import devilSpiderX.server.webServer.module.query.service.MyPasswordsService;
import devilSpiderX.server.webServer.module.query.vo.MyPasswordsVo;
import devilSpiderX.server.webServer.module.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Service
public class MyPasswordsServiceImpl implements MyPasswordsService {
    private static final Logger logger = LoggerFactory.getLogger(MyPasswordsServiceImpl.class);

    private final UserService userService;
    private final MyPasswordsMapper myPasswordsMapper;
    private final MyPasswordsDeletedMapper myPasswordsDeletedMapper;

    public MyPasswordsServiceImpl(UserService userService, MyPasswordsMapper myPasswordsMapper, MyPasswordsDeletedMapper myPasswordsDeletedMapper) {
        this.userService = userService;
        this.myPasswordsMapper = myPasswordsMapper;
        this.myPasswordsDeletedMapper = myPasswordsDeletedMapper;
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
        if (myPasswordsMapper.existsByNameAndOwner(myPwd)) {
            logger.error("在用户{}中名为({})的记录已存在", owner, name);
            return false;
        }
        myPwd.setAccount(account);
        myPwd.setPassword(MyCipher.encrypt(password));
        myPwd.setRemark(remark);
        return myPasswordsMapper.insert(myPwd) == 1;
    }

    @Override
    @Transactional
    public boolean delete(int id, String owner) {
        final MyPasswordsDeleted deletedEntity = new MyPasswordsDeleted(
                myPasswordsMapper.selectById(id)
        );
        if (!Objects.equals(deletedEntity.getOwner(), owner)) {
            throw new BaseException(AjaxCode.ENTITY_OWNER_NOT_MATCH, "实体所有者和用户不相符");
        }

        int flag = 0;
        flag += myPasswordsDeletedMapper.insert(deletedEntity);
        if (flag != 1) {
            logger.error("my_password_deleted表插入失败,id:{}", id);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

        flag += myPasswordsMapper.deleteById(id);
        if (flag != 2) {
            logger.error("my_password表删除失败,id:{}", id);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return flag == 2;
    }

    @Override
    public boolean update(int id, String name, String account, String password, String remark, String owner) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        final var oldMyPwd = myPasswordsMapper.selectById(id);
        if (!Objects.equals(oldMyPwd.getOwner(), owner)) {
            throw new BaseException(AjaxCode.ENTITY_OWNER_NOT_MATCH, "实体所有者和用户不相符");
        }

        final MyPasswords myPwd = new MyPasswords();
        myPwd.setName(name);
        myPwd.setOwner(owner);
        if (!oldMyPwd.getName().equals(name) && myPasswordsMapper.existsByNameAndOwner(myPwd)) {
            logger.error("在用户{}中名为({})的记录已存在，无法重命名为{1}", owner, name);
            return false;
        }
        myPwd.setId(id);
        myPwd.setAccount(account);
        myPwd.setPassword(MyCipher.encrypt(password));
        myPwd.setRemark(remark);
        return myPasswordsMapper.updateById(myPwd) == 1;
    }

    @Override
    public List<MyPasswordsVo> query(String name, String owner) {
        if (name == null) return _query(null, owner);
        return _query(List.of(name), owner);
    }

    @Override
    public List<MyPasswordsVo> query(String[] names, String owner) {
        if (names == null) return _query(null, owner);
        return _query(List.of(names), owner);
    }

    @Override
    public List<MyPasswordsVo> query(List<String> names, String owner) {
        return _query(names, owner);
    }

    private List<MyPasswordsVo> _query(List<String> names, String owner) {
        final List<MyPasswords> passwords = myPasswordsMapper.selectList(getQueryWrapper(names, owner));
        passwords.sort(Comparator.naturalOrder());

        final List<MyPasswordsVo> result = new ArrayList<>();
        for (final var password : passwords) {
            result.add(new MyPasswordsVo(
                    password.getId(),
                    password.getName(),
                    password.getAccount(),
                    MyCipher.decrypt(password.getPassword()),
                    password.getRemark()
            ));
        }
        return result;
    }

    @Override
    public CommonPage<MyPasswordsVo> queryPaging(String name, int length, int page, String owner) {
        if (name == null) return _queryPaging(null, length, page, owner);
        return _queryPaging(List.of(name), length, page, owner);
    }

    @Override
    public CommonPage<MyPasswordsVo> queryPaging(String[] names, int length, int page, String owner) {
        if (names == null) return _queryPaging(null, length, page, owner);
        return _queryPaging(List.of(names), length, page, owner);
    }

    @Override
    public CommonPage<MyPasswordsVo> queryPaging(List<String> names, int length, int page, String owner) {
        return _queryPaging(names, length, page, owner);
    }

    /**
     * @param page 从0开始
     */
    private CommonPage<MyPasswordsVo> _queryPaging(List<String> names, int length, int page, String owner) {
        final var passwordPage = myPasswordsMapper.selectPage(
                Page.of(page + 1, length),
                getQueryWrapper(names, owner)
        );
        final var total = passwordPage.getTotal();
        final List<MyPasswords> passwords = passwordPage.getRecords();

        passwords.sort(Comparator.naturalOrder());

        final List<MyPasswordsVo> result = new ArrayList<>();
        for (final var password : passwords) {
            result.add(new MyPasswordsVo(
                    password.getId(),
                    password.getName(),
                    password.getAccount(),
                    MyCipher.decrypt(password.getPassword()),
                    password.getRemark()
            ));
        }
        return new CommonPage<>(
                result,
                total,
                page,
                length
        );
    }

    private boolean isEmptyNames(List<String> names) {
        return names == null || names.isEmpty() || (names.size() == 1 && names.getFirst().isEmpty());
    }

    private LambdaQueryWrapper<MyPasswords> getQueryWrapper(List<String> names, String owner) {
        final var wrapper = Wrappers.lambdaQuery(MyPasswords.class);
        if (isEmptyNames(names)) {
            wrapper.eq(MyPasswords::getOwner, owner);
        } else {
            final Set<String> nameSet = new HashSet<>(names);
            final List<String> nameList = nameSet.stream()
                    .filter(name -> !Objects.equals(name, ""))
                    .toList();

            wrapper.eq(MyPasswords::getOwner, owner).and(i -> {
                for (String name : nameList) {
                    i.or().like(MyPasswords::getName, name);
                }
            });
        }
        return wrapper;
    }
}