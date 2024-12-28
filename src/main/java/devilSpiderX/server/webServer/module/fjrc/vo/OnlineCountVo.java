package devilSpiderX.server.webServer.module.fjrc.vo;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
public record OnlineCountVo(long count, String fingerprint) {
}
