package devilSpiderX.server.webServer.module.query.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import devilSpiderX.server.webServer.core.util.AjaxResp;
import devilSpiderX.server.webServer.module.query.dto.*;
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
     * <b>查询密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link GetRequestDto}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；
     * </p>
     */
    @PostMapping("get")
    @ResponseBody
    private AjaxResp<?> get(@RequestBody GetRequestDto reqBody) {
        final var uid = StpUtil.getLoginIdAsString();
        final var keys = reqBody.keys();
        final var startTime = System.currentTimeMillis();
        final var myPwdList = myPasswordsService.query(keys, uid);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户{}查询记录：{}，用时{}毫秒", uid, Arrays.toString(keys), processingTime);
        return AjaxResp.success(myPwdList);
    }


    /**
     * <b>分页查询密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link GetPagingRequestDto}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 成功；
     * </p>
     */
    @PostMapping("get_paging")
    @ResponseBody
    private AjaxResp<?> getPaging(@RequestBody GetPagingRequestDto reqBody) {
        final var uid = StpUtil.getLoginIdAsString();
        final var keys = reqBody.keys();
        final var length = reqBody.length();
        final var page = reqBody.page();
        final var startTime = System.currentTimeMillis();
        final var result = myPasswordsService.queryPaging(keys, length, page, uid);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户{}分页查询记录：{}，每页长度：{}，第{}页，用时{}毫秒", uid, Arrays.toString(keys), length, page, processingTime);
        return AjaxResp.success(result);
    }

    /**
     * <b>添加密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link AddRequestDto}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 添加成功；1 添加失败；
     * </p>
     */
    @PostMapping("add")
    @ResponseBody
    private AjaxResp<Boolean> add(@RequestBody AddRequestDto reqBody) {
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
        return AjaxResp.success(flag);
    }


    /**
     * <b>修改密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link UpdateRequestDto}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 修改成功；1 修改失败；
     * </p>
     */
    @PostMapping("update")
    @ResponseBody
    private AjaxResp<Boolean> update(@RequestBody UpdateRequestDto reqBody) {
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
        final var flag = myPasswordsService.update(id, name, account, password, remark, uid);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户{}修改记录 id：{}，用时{}毫秒", uid, id, processingTime);
        return AjaxResp.success(flag);
    }


    /**
     * <b>删除密码记录</b>
     * <p>
     * <b>应包含参数：</b>
     * {@link DeleteRequestDto}
     * </p>
     * <p>
     * <b>返回代码：</b>
     * 0 删除成功；1 删除失败；
     * </p>
     */
    @PostMapping("delete")
    @ResponseBody
    private AjaxResp<Boolean> delete(@RequestBody DeleteRequestDto reqBody) {
        if (reqBody.id() == null) {
            return AjaxResp.error("id参数不能为空或不存在");
        }
        final var id = reqBody.id();
        final var uid = StpUtil.getLoginIdAsString();
        final var startTime = System.currentTimeMillis();
        final var flag = myPasswordsService.delete(id, uid);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户{}删除记录 id：{}，用时{}毫秒", uid, id, processingTime);
        return AjaxResp.success(flag);
    }
}
