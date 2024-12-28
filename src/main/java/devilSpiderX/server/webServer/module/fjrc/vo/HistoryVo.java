package devilSpiderX.server.webServer.module.fjrc.vo;

import io.swagger.v3.oas.annotations.Hidden;

import java.util.Date;

@Hidden
public record HistoryVo(String key, Date time, String value) {
}
