package devilSpiderX.server.webServer.module.serverInfo.model.dto;


import devilSpiderX.server.webServer.module.user.model.entity.User;

public record Attribute(User user, String token) {
}
