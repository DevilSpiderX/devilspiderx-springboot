package devilSpiderX.server.webServer.core.dao;

import devilSpiderX.server.webServer.core.entity.Settings;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SettingsDao {

    Optional<Settings> findByKey(String key);

    List<Settings> findAll();

    boolean exists(String key);

    int insertOne(String key, String value);

    int insertAll(List<Settings> settingsList);

    int updateByKey(String key, String value);

}

