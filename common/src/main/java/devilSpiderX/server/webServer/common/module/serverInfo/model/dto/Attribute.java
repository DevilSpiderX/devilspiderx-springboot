package devilSpiderX.server.webServer.common.module.serverInfo.model.dto;


import devilSpiderX.server.webServer.common.module.user.model.entity.User;

public record Attribute(User user, String token) {
}
