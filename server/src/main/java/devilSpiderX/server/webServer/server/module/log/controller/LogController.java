package devilSpiderX.server.webServer.server.module.log.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import devilSpiderX.server.webServer.common.core.exception.BaseException;
import devilSpiderX.server.webServer.common.core.resp.CommonResult;
import devilSpiderX.server.webServer.common.core.resp.ResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Tag(name = "日志接口")
@SaCheckLogin
@SaCheckRole("admin")
@RestController
@RequestMapping("/api/admin/log")
public class LogController {

    @Value("${logging.file.path}")
    private String logDir;

    @Operation(summary = "获取日志列表")
    @GetMapping("list")
    public CommonResult<List<String>> list() {
        var logArray = new ArrayList<String>();
        try (Stream<Path> pathStream = Files.list(Paths.get(logDir))) {
            pathStream.forEach(path -> logArray.add(path.getFileName()
                    .toString()));
        } catch (IOException e) {
            throw new BaseException(ResultCode.Error, e);
        }
        return CommonResult.success(logArray);
    }

    @Operation(summary = "获取日志内容")
    @GetMapping("{logName}")
    public ResponseEntity<Resource> logFile(
            final
            @Parameter(description = "日志文件名")
            @PathVariable
            String logName
    ) {
        final var logFile = Paths.get(logDir, logName);
        if (Files.notExists(logFile)) {
            return ResponseEntity.notFound()
                    .build();
        }
        FileSystemResource resource = new FileSystemResource(logFile);
        MediaType contentType = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(contentType)
                .body(resource);
    }
}
