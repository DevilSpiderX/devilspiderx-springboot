package devilSpiderX.server.webServer.module.fjrc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import devilSpiderX.server.webServer.module.fjrc.entity.FjrcUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FjrcUserMapper extends BaseMapper<FjrcUser> {
}
