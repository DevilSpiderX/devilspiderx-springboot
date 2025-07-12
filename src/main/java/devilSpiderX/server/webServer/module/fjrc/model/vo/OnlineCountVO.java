package devilSpiderX.server.webServer.module.fjrc.model.vo;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
public record OnlineCountVO(long count, String fingerprint) {
}
