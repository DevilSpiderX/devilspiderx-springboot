package devilSpiderX.server.webServer.controller;

import com.alibaba.fastjson2.JSONArray;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
@RequestMapping("/log")
@SuppressWarnings("resource")
public class LogController {

    @GetMapping
    public String logListView() {
        return "log.html";
    }

    @PostMapping
    @ResponseBody
    public JSONArray list() throws IOException {
        JSONArray respList = new JSONArray();
        Files.list(Paths.get("log")).forEach(path -> respList.add(path.getFileName().toString()));
        return respList;
    }

    @GetMapping("/{logName}")
    public void logFile(@PathVariable String logName, HttpServletResponse resp) throws IOException {
        File logFile = Paths.get("log", logName).toFile();
        if (!logFile.exists()) {
            resp.sendError(404);
            return;
        }
        long fileLength = logFile.length();
        resp.setContentLengthLong(fileLength);
        resp.setContentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8).toString());
        FileInputStream fileIn = new FileInputStream(logFile);
        OutputStream webOut = resp.getOutputStream();
        int offset = 0;
        byte[] buffer = new byte[64 * 1024];
        while (offset < fileLength) {
            int count = fileIn.read(buffer);
            if (count == -1) {
                break;
            }
            offset += count;
            webOut.write(buffer, 0, count);
            webOut.flush();
        }
        fileIn.close();
    }
}
