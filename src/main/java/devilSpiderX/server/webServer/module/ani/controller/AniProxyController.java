package devilSpiderX.server.webServer.module.ani.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/ani")
public class AniProxyController {
    public static final String dataURL = "https://api.ani.rip/ani-torrent.xml";

    @RequestMapping("torrent")
    public ResponseEntity<byte[]> getTorrentXML() {
        try {
            final var url = URI.create(dataURL).toURL();
            final var con = (HttpURLConnection) url.openConnection();
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
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }
}
