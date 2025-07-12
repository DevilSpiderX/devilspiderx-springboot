package devilSpiderX.server.webServer.module.redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author DevilSpiderX
 */
public class JsonRedisTemplate extends RedisTemplate<String, Object> {

    public JsonRedisTemplate() {
        setKeySerializer(RedisSerializer.string());
        setHashKeySerializer(RedisSerializer.string());
        setValueSerializer(RedisSerializer.json());
        setHashValueSerializer(RedisSerializer.json());
    }

    public JsonRedisTemplate(RedisConnectionFactory connectionFactory) {
        this();
        setConnectionFactory(connectionFactory);
        afterPropertiesSet();
    }
}
