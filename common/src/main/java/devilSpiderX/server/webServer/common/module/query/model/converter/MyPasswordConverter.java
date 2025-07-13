package devilSpiderX.server.webServer.common.module.query.model.converter;

import devilSpiderX.server.webServer.common.module.query.model.dto.AddRequestDTO;
import devilSpiderX.server.webServer.common.module.query.model.entity.MyPassword;
import org.mapstruct.*;

/**
 * @author DevilSpiderX
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MyPasswordConverter {

    @Mappings({
            @Mapping(target = "password", source = "password"),
            @Mapping(target = "deleted", constant = "false"),
    })
    MyPassword fromDTO(AddRequestDTO dto, String password, long owner);

}
