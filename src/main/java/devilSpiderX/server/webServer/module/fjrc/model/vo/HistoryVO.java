package devilSpiderX.server.webServer.module.fjrc.model.vo;

import io.swagger.v3.oas.annotations.Hidden;

import java.util.Date;

@Hidden
public record HistoryVO(String key, Date time, String value) {
}
