package devilSpiderX.server.webServer.common.module.query.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "查询密码记录请求参数")
public record GetRequestDTO(

        @Schema(description = "查询值")
        @NotNull(message = "查询值List不能为null")
        List<String> keys

) {
}
