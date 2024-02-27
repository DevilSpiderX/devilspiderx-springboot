package devilSpiderX.server.webServer.module.ani.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
                con.setDoOutput(true);
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
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("连接错误".getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }
}
