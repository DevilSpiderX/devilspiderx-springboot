package devilSpiderX.server.webServer.module.query.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import devilSpiderX.server.webServer.core.vo.AjaxResp;
import devilSpiderX.server.webServer.core.vo.CommonPage;
import devilSpiderX.server.webServer.module.query.dto.*;
import devilSpiderX.server.webServer.module.query.service.MyPasswordsService;
import devilSpiderX.server.webServer.module.query.vo.MyPasswordsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Tag(name = "密码记录接口")
@Controller
@RequestMapping("/api/query")
@SaCheckLogin
public class QueryController {
    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    private final MyPasswordsService myPasswordsService;

    public QueryController(MyPasswordsService myPasswordsService) {
        this.myPasswordsService = myPasswordsService;
    }

    @Operation(summary = "查询密码记录")
    @PostMapping("get")
    @ResponseBody
    private AjaxResp<List<MyPasswordsVo>> get(
            @Parameter(description = "查询参数")
            @RequestBody GetRequestDto reqBody
    ) {
        final var uid = StpUtil.getLoginIdAsString();
        final var keys = reqBody.keys();
        final var startTime = System.currentTimeMillis();
        final var myPwdList = myPasswordsService.query(keys, uid);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户{}查询记录：{}，用时{}毫秒", uid, Arrays.toString(keys), processingTime);
        return AjaxResp.success(myPwdList);
    }


    @Operation(summary = "分页查询密码记录")
    @PostMapping("get_paging")
    @ResponseBody
    private AjaxResp<CommonPage<MyPasswordsVo>> getPaging(
            @Parameter(description = "查询参数")
            @RequestBody GetPagingRequestDto reqBody
    ) {
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

    @Operation(summary = "添加密码记录")
    @PostMapping("add")
    @ResponseBody
    private AjaxResp<Boolean> add(
            @Parameter(description = "添加密码记录的数据")
            @RequestBody AddRequestDto reqBody
    ) {
        Objects.requireNonNull(reqBody.name(), "必须存在name参数");
        if (reqBody.name().isBlank()) {
            throw new IllegalArgumentException("name参数不能为空");
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

    @Operation(summary = "修改密码记录")
    @PostMapping("update")
    @ResponseBody
    private AjaxResp<Boolean> update(
            @Parameter(description = "修改密码记录的数据")
            @RequestBody UpdateRequestDto reqBody
    ) {
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

    @Operation(summary = "删除密码记录")
    @PostMapping("delete")
    @ResponseBody
    private AjaxResp<Boolean> delete(
            @Parameter(description = "删除密码记录的ID")
            @RequestBody DeleteRequestDto reqBody
    ) {
        final var id = reqBody.id();
        final var uid = StpUtil.getLoginIdAsString();
        final var startTime = System.currentTimeMillis();
        final var flag = myPasswordsService.delete(id, uid);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户{}删除记录 id：{}，用时{}毫秒", uid, id, processingTime);
        return AjaxResp.success(flag);
    }
}
