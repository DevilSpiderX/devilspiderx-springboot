package devilSpiderX.server.webServer.module.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import devilSpiderX.server.webServer.module.user.entity.UserPermission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserPermissionMapper extends BaseMapper<UserPermission> {
}
