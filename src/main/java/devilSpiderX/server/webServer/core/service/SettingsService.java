package devilSpiderX.server.webServer.core.service;

import devilSpiderX.server.webServer.core.entity.Settings;

import java.util.List;

public interface SettingsService {
    String get(int id);

    String get(String key);

    List<Settings> getAll();

    boolean set(int id, String value);

    boolean set(String key, String value);

    boolean exist(int id);

    boolean exist(String key);
}
