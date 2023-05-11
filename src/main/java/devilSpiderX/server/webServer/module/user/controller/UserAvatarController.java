package devilSpiderX.server.webServer.module.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/user/avatar")
public class UserAvatarController {
    private final Logger logger = LoggerFactory.getLogger(UserAvatarController.class);

    @RequestMapping("{imageName}")
    public ResponseEntity<Resource> get(@PathVariable final String imageName,
                                        @Value("#{DSXProperties.avatarDirPath}") final String avatarDirPath) {
        final Path imagePath = Paths.get(avatarDirPath, imageName);
        if (Files.notExists(imagePath)) {
            return ResponseEntity.notFound().build();
        }

        final FileSystemResource resource = new FileSystemResource(imagePath);

        return ResponseEntity.ok()
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.IMAGE_JPEG))
                .body(resource);
    }
}
