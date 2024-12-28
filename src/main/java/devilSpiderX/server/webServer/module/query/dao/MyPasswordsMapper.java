package devilSpiderX.server.webServer.module.query.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import devilSpiderX.server.webServer.module.query.entity.MyPasswords;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyPasswordsMapper extends BaseMapper<MyPasswords> {
    default boolean existsByNameAndOwner(MyPasswords myPasswords) {
        final var wrapper = Wrappers.lambdaQuery(MyPasswords.class)
                .eq(MyPasswords::getName, myPasswords.getName())
                .eq(MyPasswords::getOwner, myPasswords.getOwner());
        return this.exists(wrapper);
    }
}
