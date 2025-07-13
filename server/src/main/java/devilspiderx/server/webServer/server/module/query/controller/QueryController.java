package devilSpiderX.server.webServer.server.module.query.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import devilSpiderX.server.webServer.common.core.constant.PageConstant;
import devilSpiderX.server.webServer.common.core.resp.CommonPage;
import devilSpiderX.server.webServer.common.core.resp.CommonResult;
import devilSpiderX.server.webServer.common.module.query.model.dto.AddRequestDTO;
import devilSpiderX.server.webServer.common.module.query.model.dto.GetRequestDTO;
import devilSpiderX.server.webServer.common.module.query.model.dto.UpdateRequestDTO;
import devilSpiderX.server.webServer.common.module.query.model.vo.MyPasswordsVO;
import devilSpiderX.server.webServer.server.module.query.service.MyPasswordsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "密码记录接口")
@SaCheckLogin
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/query")
public class QueryController {
    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    private final MyPasswordsService myPasswordsService;

    @Operation(summary = "查询密码记录")
    @PostMapping("get")
    private CommonResult<List<MyPasswordsVO>> get(
            final
            @Valid
            @RequestBody
            GetRequestDTO reqBody
    ) {
        final var uid = StpUtil.getLoginIdAsString();
        final var keys = reqBody.keys();
        final var startTime = System.currentTimeMillis();
        final var myPwdList = myPasswordsService.query(keys);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户(uid={})查询记录：{}，用时{}毫秒", uid, keys, processingTime);
        return CommonResult.success(myPwdList);
    }


    @Operation(summary = "分页查询密码记录")
    @PostMapping("page")
    private CommonResult<CommonPage<MyPasswordsVO>> getPaging(
            final
            @Parameter(description = "当前页码")
            @RequestParam(defaultValue = PageConstant.DEFAULT_CURRENT_STR)
            int current,

            final
            @Parameter(description = "每页数据数量")
            @RequestParam(defaultValue = PageConstant.DEFAULT_PAGE_SIZE_STR)
            int pageSize,

            final
            @Valid
            @RequestBody
            GetRequestDTO reqBody
    ) {
        final var uid = StpUtil.getLoginIdAsString();
        final var keys = reqBody.keys();
        final var startTime = System.currentTimeMillis();
        final var result = myPasswordsService.queryPaging(keys, current, pageSize);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info(
                "用户(uid={})分页查询记录：{}，每页长度：{}，第{}页，用时{}毫秒",
                uid,
                keys,
                result.getPageSize(),
                result.getCurrent(),
                processingTime
        );
        return CommonResult.success(result);
    }

    @Operation(summary = "添加密码记录")
    @PostMapping("add")
    private CommonResult<Void> add(
            final
            @Valid
            @RequestBody
            AddRequestDTO reqBody
    ) {
        final var owner = StpUtil.getLoginIdAsString();
        final var startTime = System.currentTimeMillis();
        myPasswordsService.add(reqBody);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户(uid={})添加记录：{}，用时{}毫秒", owner, reqBody.name(), processingTime);
        return CommonResult.success();
    }

    @Operation(summary = "修改密码记录")
    @PostMapping("update/{id:\\d+}")
    private CommonResult<Void> update(
            final
            @Parameter(description = "密码记录id")
            @PathVariable
            int id,

            final
            @RequestBody
            @Valid
            UpdateRequestDTO reqBody
    ) {
        final var uid = StpUtil.getLoginIdAsString();

        final var startTime = System.currentTimeMillis();
        myPasswordsService.update(id, reqBody);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户(uid={})修改记录 id：{}，用时{}毫秒", uid, id, processingTime);
        return CommonResult.success();
    }

    @Operation(summary = "删除密码记录")
    @GetMapping("delete/{id:\\d+}")
    private CommonResult<Void> delete(
            final
            @Parameter(description = "密码记录id")
            @PathVariable
            int id
    ) {
        final var uid = StpUtil.getLoginIdAsString();
        final var startTime = System.currentTimeMillis();
        myPasswordsService.delete(id);

        final var processingTime = System.currentTimeMillis() - startTime;
        logger.info("用户(uid={})删除记录 id：{}，用时{}毫秒", uid, id, processingTime);
        return CommonResult.success();
    }
}
