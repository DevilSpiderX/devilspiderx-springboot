package devilSpiderX.server.webServer.module.query.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.util.SqlUtil;
import devilSpiderX.server.webServer.core.exception.BaseException;
import devilSpiderX.server.webServer.core.resp.CommonPage;
import devilSpiderX.server.webServer.core.resp.ResultCode;
import devilSpiderX.server.webServer.core.util.DigestUtils;
import devilSpiderX.server.webServer.core.util.ValidUtil;
import devilSpiderX.server.webServer.module.mybatisflex.UpdateChainExt;
import devilSpiderX.server.webServer.module.query.model.converter.MyPasswordConverter;
import devilSpiderX.server.webServer.module.query.model.dto.AddRequestDTO;
import devilSpiderX.server.webServer.module.query.model.dto.UpdateRequestDTO;
import devilSpiderX.server.webServer.module.query.model.mapper.MyPasswordMapper;
import devilSpiderX.server.webServer.module.query.model.vo.MyPasswordsVO;
import devilSpiderX.server.webServer.module.query.service.MyPasswordsService;
import devilSpiderX.server.webServer.module.satoken.StpKit;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

import static devilSpiderX.server.webServer.module.query.model.entity.table.MyPasswordTableDef.MY_PASSWORD;

@RequiredArgsConstructor
@Service
public class MyPasswordsServiceImpl implements MyPasswordsService {
    private static final Logger logger = LoggerFactory.getLogger(MyPasswordsServiceImpl.class);

    private final ValidUtil validUtil;
    private final MyPasswordMapper myPasswordMapper;
    private final MyPasswordConverter myPasswordConverter;


    @Transactional
    @Override
    public void add(final @Nonnull AddRequestDTO dto) {
        Assert.notNull(dto, "dto不能为null");
        validUtil.validate(dto);

        final var owner = StpKit.USER.getLoginIdAsLong();
        final var exists = QueryChain.of(myPasswordMapper)
                .where(MY_PASSWORD.NAME.eq(dto.name())
                        .and(MY_PASSWORD.OWNER.eq(owner)))
                .exists();
        if (exists) {
            logger.error("在用户{}中名为({})的记录已存在", owner, dto.name());
            throw new BaseException(ResultCode.MyPasswordExists);
        }

        final var password = DigestUtils.aesEncrypt(dto.password());
        final var myPwd = myPasswordConverter.fromDTO(dto, password, owner);

        final var n = myPasswordMapper.insert(myPwd);
        if (!SqlUtil.toBool(n)) {
            logger.info("用户(uid={})密码记录(name={})添加失败", owner, dto.name());
            throw new BaseException(ResultCode.MyPasswordAddFailure);
        }
        logger.info("用户(uid={})密码记录(id={},name={})添加成功", owner, myPwd.getId(), dto.name());
    }

    @Transactional
    @Override
    public void delete(final int id) {
        final var owner = StpKit.USER.getLoginIdAsLong();

        final var myPwd = QueryChain.of(myPasswordMapper)
                .where(MY_PASSWORD.ID.eq(id)
                        .and(MY_PASSWORD.OWNER.eq(owner)))
                .forUpdate()
                .one();
        if (myPwd == null) {
            throw new BaseException(ResultCode.MyPasswordNotExists);
        }

        final var n = myPasswordMapper.deleteById(id);
        if (!SqlUtil.toBool(n)) {
            logger.info("用户(uid={})删除密码记录(id={})失败", owner, id);
            throw new BaseException(ResultCode.MyPasswordDeleteFailure);
        }
        logger.info("用户(uid={})删除密码记录(id={})成功", owner, id);
    }

    @Transactional
    @Override
    public void update(final int id, final @Nonnull UpdateRequestDTO dto) {
        Assert.notNull(dto, "dto不能为null");

        final var owner = StpKit.USER.getLoginIdAsLong();

        final var myPwd = QueryChain.of(myPasswordMapper)
                .where(MY_PASSWORD.ID.eq(id)
                        .and(MY_PASSWORD.OWNER.eq(owner)))
                .forUpdate()
                .one();
        if (myPwd == null) {
            throw new BaseException(ResultCode.MyPasswordNotExists);
        }

        final var updateChain = UpdateChainExt.of(myPasswordMapper);
        if (StringUtils.isNotBlank(dto.name())) {
            final var name = dto.name();
            final var exists = QueryChain.of(myPasswordMapper)
                    .where(MY_PASSWORD.NAME.eq(name)
                            .and(MY_PASSWORD.OWNER.eq(owner)))
                    .exists();
            if (exists) {
                logger.error("在用户(uid={})中名为({})的记录已存在，无法重命名为{}", owner, name, name);
                throw new BaseException(ResultCode.MyPasswordExists);
            }
            updateChain.set(MY_PASSWORD.NAME, name);
        }

        if (Objects.nonNull(dto.password())) {
            final var password = DigestUtils.aesEncrypt(dto.password());
            updateChain.set(MY_PASSWORD.PASSWORD, password);
        }

        updateChain.set(MY_PASSWORD.ACCOUNT, dto.account(), Objects::nonNull)
                .set(MY_PASSWORD.REMARK, dto.remark(), Objects::nonNull);

        if (!updateChain.isModified()) {
            logger.info("用户(uid={})密码记录(id={})未修改", owner, id);
            return;
        }

        final var flag = updateChain.update();
        if (!flag) {
            logger.info("用户(uid={})密码记录(id={})修改失败", owner, id);
            throw new BaseException(ResultCode.MyPasswordUpdateFailure);
        }
        logger.info("用户(uid={})密码记录(id={})修改成功", owner, id);
    }

    @Override
    public @Nonnull List<MyPasswordsVO> query(List<String> names) {
        final var owner = StpKit.USER.getLoginIdAsLong();
        final var passwords = QueryChain.of(myPasswordMapper)
                .where(getQueryWrapper(names, owner))
                .orderBy(MY_PASSWORD.ID.asc())
                .listAs(MyPasswordsVO.class);

        for (final var vo : passwords) {
            final var password = vo.getPassword();
            vo.setPassword(DigestUtils.aesDecrypt(password));
        }

        return passwords;
    }

    @Override
    public @Nonnull CommonPage<MyPasswordsVO> queryPaging(List<String> names, int current, int pageSize) {
        final var owner = StpKit.USER.getLoginIdAsLong();
        final var passwords = QueryChain.of(myPasswordMapper)
                .where(getQueryWrapper(names, owner))
                .orderBy(MY_PASSWORD.ID.asc())
                .pageAs(Page.of(current, pageSize), MyPasswordsVO.class);

        for (final var vo : passwords.getRecords()) {
            final var password = vo.getPassword();
            vo.setPassword(DigestUtils.aesDecrypt(password));
        }

        return new CommonPage<>(passwords);
    }

    private boolean isEmptyNames(List<String> names) {
        return names == null || names.isEmpty() || (names.size() == 1 && StringUtils.isBlank(names.getFirst()));
    }

    private @Nonnull QueryCondition getQueryWrapper(List<String> names, long owner) {
        final var condition = MY_PASSWORD.OWNER.eq(owner);
        if (!isEmptyNames(names)) {
            final var nameCondition = QueryCondition.createEmpty();
            names.stream()
                    .filter(name -> !Objects.equals(name, ""))
                    .distinct()
                    .forEach(name -> nameCondition.or(MY_PASSWORD.NAME.like(name)));
            condition.and(nameCondition);
        }
        return condition;
    }
}