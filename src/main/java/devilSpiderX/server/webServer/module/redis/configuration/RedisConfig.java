package devilSpiderX.server.webServer.module.redis.configuration;

import devilSpiderX.server.webServer.module.redis.JsonRedisTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * @author DevilSpiderX
 */
@Configuration
public class RedisConfig {

    @Bean
    public JsonRedisTemplate jsonRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new JsonRedisTemplate(redisConnectionFactory);
    }

}
