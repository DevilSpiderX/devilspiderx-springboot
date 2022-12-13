package devilSpiderX.server.webServer.module.log.controller;

import devilSpiderX.server.webServer.core.util.AjaxResp;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

@Controller
@RequestMapping("/api/admin/log")
@SuppressWarnings("resource")
public class LogController {
    static final String logDir = "./log";

    @RequestMapping("/list")
    @ResponseBody
    public AjaxResp<?> list() throws IOException {
        var logArray = new ArrayList<>();
        Files.list(Paths.get(logDir)).forEach(path -> logArray.add(path.getFileName().toString()));
        return AjaxResp.success(logArray);
    }

    @GetMapping("/{logName}")
    public ResponseEntity<Resource> logFile(@PathVariable String logName) {
        File logFile = Paths.get(logDir, logName).toFile();
        if (!logFile.exists()) {
            return ResponseEntity.notFound().build();
        }
        FileSystemResource resource = new FileSystemResource(logFile);
        MediaType contentType = new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(contentType)
                .body(resource);
    }
}
