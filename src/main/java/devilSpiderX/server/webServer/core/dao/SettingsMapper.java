package devilSpiderX.server.webServer.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import devilSpiderX.server.webServer.core.entity.Settings;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface SettingsMapper extends BaseMapper<Settings> {

    default Optional<Settings> findByKey(String key) {
        final var wrapper = Wrappers.lambdaQuery(Settings.class);
        wrapper.eq(Settings::getKey, key);

        return Optional.ofNullable(this.selectOne(wrapper));
    }

}

