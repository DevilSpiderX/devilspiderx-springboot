package devilSpiderX.server.webServer.common.module.redis;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis操作工具类</br>
 * 写这个工具类时，没考虑在事务中使用
 *
 * @author DevilSpiderX
 */
@RequiredArgsConstructor
@Component
public final class RedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private final JsonRedisTemplate redisTemplate;


    private void _assert(final @Nullable Object result) {
        if (result == null) {
            throw new IllegalStateException("use in transaction");
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public boolean expire(final @Nonnull String key, long time) {
        if (time <= 0) {
            throw new IllegalArgumentException("time must be greater than 0");
        }
        final var result = redisTemplate.expire(key, time, TimeUnit.SECONDS);
        _assert(result);
        return result;
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0 代表为永久有效
     */
    public long getExpire(final @Nonnull String key) {
        final var result = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        _assert(result);
        return result;
    }

    // ===============================string=================================

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(final @Nonnull String key) {
        final var result = redisTemplate.hasKey(key);
        _assert(result);
        return result;
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void delete(String... key) {
        if (key != null && key.length > 0) {
            final var result = redisTemplate.delete(List.of(key));
            _assert(result);
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(final @Nonnull String key) {
        try {
            final var value = redisTemplate.boundValueOps(key)
                    .get();
            if (value instanceof Wrapper<?>(Object _value)) {
                return (T) _value;
            }
            return (T) value;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(final @Nonnull String key, final @Nonnull Object value) {
        try {
            redisTemplate.boundValueOps(key)
                    .set(Wrapper.of(value));
            return true;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(
            final @Nonnull String key,
            final @Nonnull Object value,
            long time
    ) {
        if (time <= 0) {
            return set(key, value);
        }
        try {
            redisTemplate.boundValueOps(key)
                    .set(Wrapper.of(value), time, TimeUnit.SECONDS);
            return true;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return 增加后的值
     */
    public long increment(final @Nonnull String key, long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递增因子不得小于0");
        }
        final var result = redisTemplate.boundValueOps(key)
                .increment(delta);
        _assert(result);
        return result;
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return 减少后的值
     */
    public long decrement(final @Nonnull String key, long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递减因子不得小于0");
        }
        final var result = redisTemplate.boundValueOps(key)
                .decrement(delta);
        _assert(result);
        return result;
    }

    /**
     * 发现{@link RedisUtil#set(String, Object)}方法无法正确序列化List类型</br>
     * 所以写这个包装类用于包装序列化错误的类型
     *
     * @param value 要包装的值
     * @param <T>   要包装的类型
     */
    public record Wrapper<T>(T value) {
        public static <T> Wrapper<T> of(T value) {
            return new Wrapper<>(value);
        }
    }

}
