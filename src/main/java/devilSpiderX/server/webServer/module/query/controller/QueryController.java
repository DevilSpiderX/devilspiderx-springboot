package devilSpiderX.server.webServer.module.query.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import devilSpiderX.server.webServer.module.query.service.MyPasswordsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

@Controller
@RequestMapping("/api/query")
@SaCheckLogin
public class QueryController {
    private final Logger logger = LoggerFactory.getLogger(QueryController.class);
    private final MyPasswordsService myPasswordsService;

    public QueryController(MyPasswordsService myPasswordsService) {
        this.myPasswordsService = myPasswordsService;
    }

    /**
     * 查询密码记录请求参数
     *
     * @param key 查询值
     */
    record GetRequest(String key) {
        /**
         * 分割查询值,使用空格和<code>.</code>来分割
         *
         * @return 分割后的查询值
         */
        public String[] keys() {
            if (key != null) {
                final var keysStr = key.trim();
                return keysStr.split("(\\s|\\.)+");
            }
            return new String[0];
        }
    }

    /**
     * <b>查询密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link GetRequest}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；
     * </p>
     */
    @PostMapping("get")
    @ResponseBody
    private AjaxResp<?> get(@RequestBody GetRequest reqBody) {
        final var uid = StpUtil.getLoginIdAsString();
        final var keys = reqBody.keys();
        final var startTime = System.currentTimeMillis();
        final var myPwdList = myPasswordsService.query(keys, uid);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户{}查询记录：{}，用时{}毫秒", uid, Arrays.toString(keys), processingTime);
        return AjaxResp.success(myPwdList);
    }

    /**
     * 分页查询密码记录请求参数
     *
     * @param key    查询值
     * @param length 每页的长度
     * @param page   查询第n页
     */
    record GetPagingRequest(String key, Integer length, Integer page) {
        public GetPagingRequest(String key, Integer length, Integer page) {
            this.key = key;
            this.length = length == null ? 20 : length;
            this.page = page == null ? 0 : page;
        }

        /**
         * 分割查询值,使用空格和<code>.</code>来分割
         *
         * @return 分割后的查询值
         */
        public String[] keys() {
            if (key != null) {
                final var keysStr = key.trim();
                return keysStr.split("(\\s|\\.)+");
            }
            return new String[0];
        }
    }

    /**
     * <b>分页查询密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link GetPagingRequest}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；
     * </p>
     */
    @PostMapping("get_paging")
    @ResponseBody
    private AjaxResp<?> getPaging(@RequestBody GetPagingRequest reqBody) {
        final var uid = StpUtil.getLoginIdAsString();
        final var keys = reqBody.keys();
        final var length = reqBody.length();
        final var page = reqBody.page();
        final var startTime = System.currentTimeMillis();
        final var result = myPasswordsService.queryPaging(keys, length, page, uid);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户{}分页查询记录：{}，每页长度：{}，第{}页，用时{}毫秒", uid, Arrays.toString(keys), length, page, processingTime);
        return AjaxResp.success(result.list())
                .setDataCount(result.dataCount());
    }

    /**
     * 添加密码记录请求参数
     *
     * @param name     名称
     * @param account  账号
     * @param password 密码
     * @param remark   备注
     */
    record AddRequest(String name, String account, String password, String remark) {
    }

    /**
     * <b>添加密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link AddRequest}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 添加成功；1 添加失败；
     * </p>
     */
    @PostMapping("add")
    @ResponseBody
    private AjaxResp<?> add(@RequestBody AddRequest reqBody) {
        if (reqBody.name() == null) {
            return AjaxResp.error("name参数不能为空或不存在");
        }
        final var name = reqBody.name();
        final var account = reqBody.account();
        final var password = reqBody.password();
        final var remark = reqBody.remark();
        final var owner = StpUtil.getLoginIdAsString();
        final var startTime = System.currentTimeMillis();
        final var flag = myPasswordsService.add(name, account, password, remark, owner);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户{}添加记录：{}，用时{}毫秒", owner, name, processingTime);
        return flag ? AjaxResp.success()
                : AjaxResp.failure();
    }

    /**
     * 修改密码记录请求参数
     *
     * @param id       记录id
     * @param name     名称(可选)
     * @param account  账号(可选)
     * @param password 密码(可选)
     * @param remark   备注(可选)
     */
    record UpdateRequest(Integer id, String name, String account, String password, String remark) {
    }

    /**
     * <b>修改密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link UpdateRequest}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 修改成功；1 修改失败；
     * </p>
     */
    @PostMapping("update")
    @ResponseBody
    private AjaxResp<?> update(@RequestBody UpdateRequest reqBody) {
        if (reqBody.id() == null) {
            return AjaxResp.error("id参数不能为空或不存在");
        }
        final var id = reqBody.id();
        final var uid = StpUtil.getLoginIdAsString();
        final var name = reqBody.name();
        final var account = reqBody.account();
        final var password = reqBody.password();
        final var remark = reqBody.remark();
        final var startTime = System.currentTimeMillis();
        final var flag = myPasswordsService.update(id, name, account, password, remark);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户{}修改记录 id：{}，用时{}毫秒", uid, id, processingTime);
        return flag ? AjaxResp.success()
                : AjaxResp.failure();
    }

    /**
     * 删除密码记录请求参数
     *
     * @param id 记录id
     */
    record DeleteRequest(Integer id) {
    }

    /**
     * <b>删除密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link DeleteRequest}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 删除成功；1 删除失败；
     * </p>
     */
    @PostMapping("delete")
    @ResponseBody
    private AjaxResp<?> delete(@RequestBody DeleteRequest reqBody) {
        if (reqBody.id() == null) {
            return AjaxResp.error("id参数不能为空或不存在");
        }
        final var id = reqBody.id();
        final var uid = StpUtil.getLoginIdAsString();
        final var startTime = System.currentTimeMillis();
        final var flag = myPasswordsService.delete(id);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户{}删除记录 id：{}，用时{}毫秒", uid, id, processingTime);
        return flag ? AjaxResp.success()
                : AjaxResp.failure();
    }
}
