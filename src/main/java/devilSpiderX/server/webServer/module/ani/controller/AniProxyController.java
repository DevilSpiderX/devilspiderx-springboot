package devilSpiderX.server.webServer.module.ani.controller;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/ani")
public class AniProxyController {
    public static final String dataURL = "https://api.ani.rip/ani-torrent.xml";
    public static final Proxy PROXY = new Proxy(
            Proxy.Type.HTTP,
            new InetSocketAddress("localhost", 10809)
    );

    /**
     * 获取Ani番剧更新列表
     *
     * @param proxy 是否使用代理
     * @return 番剧更新列表
     */
    @RequestMapping("torrent")
    public ResponseEntity<byte[]> getTorrentXML(@RequestParam(name = "proxy", defaultValue = "false") boolean proxy) {
        try {
            final var url = URI.create(dataURL).toURL();
            final var urlCon = proxy ? url.openConnection(PROXY) : url.openConnection();
            if (urlCon instanceof HttpURLConnection con) {
                con.setRequestMethod("GET");
                con.setDoOutput(false);
                con.setDoInput(true);
                con.setUseCaches(false);
                var respCode = con.getResponseCode();
                if (respCode != HttpURLConnection.HTTP_OK) {
                    return ResponseEntity.notFound().build();
                }
                final var contentType = con.getHeaderField("Content-Type");
                try (BufferedInputStream in = new BufferedInputStream(con.getInputStream())) {
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .body(in.readAllBytes());
                }
            } else {
                return ResponseEntity.internalServerError()
                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                        .body("连接错误".getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                    .body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

    @GetMapping("file/{name}")
    public ResponseEntity<byte[]> getTorrentFile(
            @PathVariable final String name,
            @RequestParam("fileUrl") final String fileUrl
    ) {
        if (!StpUtil.isLogin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                    .body("用户未登录".getBytes(StandardCharsets.UTF_8));
        }
        if (!StpUtil.hasPermission("ani.download")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                    .body("用户没有下载的权限".getBytes(StandardCharsets.UTF_8));
        }

        try {
            final var url = URI.create(fileUrl).toURL();
            final var urlCon = url.openConnection(PROXY);
            if (urlCon instanceof HttpURLConnection con) {
                con.setRequestMethod("GET");
                con.setDoOutput(false);
                con.setDoInput(true);
                con.setUseCaches(true);
                var respCode = con.getResponseCode();
                if (respCode != HttpURLConnection.HTTP_OK) {
                    return ResponseEntity.notFound().build();
                }
                final var contentType = con.getHeaderField("Content-Type");
                final var headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.setContentDisposition(
                        ContentDisposition.attachment()
                                .filename(UriUtils.encode(name + ".torrent", StandardCharsets.UTF_8))
                                .build()
                );
                try (BufferedInputStream in = new BufferedInputStream(con.getInputStream())) {
                    return ResponseEntity.ok()
                            .headers(headers)
                            .body(in.readAllBytes());
                }
            } else {
                return ResponseEntity.internalServerError()
                        .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                        .body("连接错误".getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                    .body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }
}
