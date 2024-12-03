package devilSpiderX.server.webServer.module.user.dto;

public record UpdatePasswordRequest(String oldPassword, String newPassword) {
}
