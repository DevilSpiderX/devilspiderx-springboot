package devilSpiderX.server.webServer.module.user.controller;

import devilSpiderX.server.webServer.core.property.DSXProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Tag(name = "用户头像接口")
@Controller
@RequestMapping("/user/avatar")
public class UserAvatarController {
    private final Path avatarDirPath;

    public UserAvatarController(final DSXProperties dsxProperties) {
        this.avatarDirPath = Paths.get(dsxProperties.getAvatarDirPath());
    }


    @Operation(summary = "获取用户头像")
    @GetMapping("{imageName}")
    public ResponseEntity<Resource> get(
            @Parameter(description = "用户头像地址")
            @PathVariable final String imageName
    ) {
        final Path imagePath = avatarDirPath.resolve(imageName);

        if (Files.notExists(imagePath)) {
            return ResponseEntity.notFound().build();
        }

        final FileSystemResource resource = new FileSystemResource(imagePath);

        return ResponseEntity.ok()
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.IMAGE_JPEG))
                .body(resource);
    }
}
