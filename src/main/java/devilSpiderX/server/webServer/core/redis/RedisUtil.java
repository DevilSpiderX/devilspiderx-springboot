package devilSpiderX.server.webServer.core.redis;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public final class RedisUtil {
    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(final RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private void _assert(@Nullable Object result) {
        if (result == null) {
            throw new IllegalArgumentException("Do not use in transaction");
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public boolean expire(@Nonnull String key, long time) {
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
    public long getExpire(@Nonnull String key) {
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
    public boolean hasKey(@Nonnull String key) {
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
    public <T> T get(@Nonnull String key) {
        try {
            final var value = redisTemplate.opsForValue()
                    .get(key);
            return (T) value;
        } catch (ClassCastException e) {
            throw e;
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
    public boolean set(@Nonnull String key, @Nonnull Object value) {
        try {
            redisTemplate.opsForValue()
                    .set(key, value);
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
            @Nonnull String key,
            @Nonnull Object value,
            long time
    ) {
        if (time <= 0) {
            return set(key, value);
        }
        try {
            redisTemplate.opsForValue()
                    .set(key, value, time, TimeUnit.SECONDS);
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
    public long increment(@Nonnull String key, long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递增因子必须大于0");
        }
        final var result = redisTemplate.opsForValue()
                .increment(key, delta);
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
    public long decrement(String key, long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递减因子必须大于0");
        }
        final var result = redisTemplate.opsForValue()
                .increment(key, -delta);
        _assert(result);
        return result;
    }

    // ================================hash=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <T> T hashGet(@Nonnull String key, @Nonnull String item) {
        try {
            final var value = redisTemplate.opsForHash()
                    .get(key, item);
            return (T) value;
        } catch (ClassCastException e) {
            throw e;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public <V> Map<String, V> hashMapGet(@Nonnull String key) {
        final var result = redisTemplate.<String, V>opsForHash()
                .entries(key);
        _assert(result);
        return result;
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public <V> boolean hashMapSet(@Nonnull String key, @Nonnull Map<String, V> map) {
        try {
            redisTemplate.<String, V>opsForHash()
                    .putAll(key, map);
            return true;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public <V> boolean hashMapSet(
            @Nonnull String key,
            @Nonnull Map<String, V> map,
            long time
    ) {
        if (!hashMapSet(key, map)) {
            return false;
        }
        if (time > 0 && !expire(key, time)) {
            logger.warn("hMSet中expire执行失败,key: {}", key);
        }
        return true;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key     键
     * @param hashKey 项
     * @param value   值
     * @return true 成功 false失败
     */
    public <V> boolean hashSet(@Nonnull String key, @Nonnull String hashKey, V value) {
        try {
            redisTemplate.<String, V>opsForHash()
                    .put(key, hashKey, value);
            return true;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key     键
     * @param hashKey 项
     * @param value   值
     * @param time    时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public <V> boolean hashSet(@Nonnull String key, @Nonnull String hashKey, V value, long time) {
        if (!hashSet(key, hashKey, value)) {
            return false;
        }
        if (time > 0 && !expire(key, time)) {
            logger.warn("hSet中expire执行失败,key: {}", key);
        }
        return true;
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hashDelete(@Nonnull String key, @Nonnull String... item) {
        final var result = redisTemplate.opsForHash()
                .delete(key, (Object[]) item);
        _assert(result);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hashHasKey(@Nonnull String key, @Nonnull String item) {
        final var result = redisTemplate.opsForHash()
                .hasKey(key, item);
        _assert(result);
        return result;
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key   键
     * @param item  项
     * @param delta 要增加几(大于0)
     * @return 增加后的值
     */
    public long hashIncrement(@Nonnull String key, @Nonnull String item, long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递增因子必须大于0");
        }
        final var result = redisTemplate.opsForHash()
                .increment(key, item, delta);
        _assert(result);
        return result;
    }

    /**
     * hash递减
     *
     * @param key   键
     * @param item  项
     * @param delta 要减少几(大于0)
     * @return 减小后的值
     */
    public long hashDecrement(@Nonnull String key, @Nonnull String item, long delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("递减因子必须大于0");
        }
        final var result = redisTemplate.opsForHash()
                .increment(key, item, -delta);
        _assert(result);
        return result;
    }
    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return 需要的值
     */
    @SuppressWarnings("unchecked")
    public <V> Set<V> setGet(@Nonnull String key) {
        try {
            final var result = redisTemplate.opsForSet()
                    .members(key);
            _assert(result);
            return (Set<V>) result;
        } catch (ClassCastException e) {
            throw e;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return Collections.emptySet();
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean setHasKey(@Nonnull String key, @Nonnull Object value) {
        try {
            final var result = redisTemplate.opsForSet()
                    .isMember(key, value);
            _assert(result);
            return result;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long setSet(@Nonnull String key, Object... values) {
        try {
            final var result = redisTemplate.opsForSet()
                    .add(key, values);
            _assert(result);
            return result;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long setSetAndTime(@Nonnull String key, long time, Object... values) {
        final var result = setSet(key, values);
        if (time > 0 && !expire(key, time)) {
            logger.warn("sSetAndTime中expire执行失败,key: {}", key);
        }
        return result;
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return set缓存的长度
     */
    public long setSize(@Nonnull String key) {
        try {
            final var result = redisTemplate.opsForSet()
                    .size(key);
            _assert(result);
            return result;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return -1;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(@Nonnull String key, Object... values) {
        try {
            final var result = redisTemplate.opsForSet()
                    .remove(key, values);
            _assert(result);
            return result;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
    }
    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 (0 到 -1 代表所有值)
     * @return list缓存
     */
    @SuppressWarnings("unchecked")
    public <V> List<V> listGet(@Nonnull String key, long start, long end) {
        try {
            final var result = redisTemplate.opsForList()
                    .range(key, start, end);
            _assert(result);
            return (List<V>) result;
        } catch (ClassCastException e) {
            throw e;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return list缓存的长度
     */
    public long listSize(@Nonnull String key) {
        try {
            final var result = redisTemplate.opsForList()
                    .size(key);
            _assert(result);
            return result;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return -1;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return 值
     */
    @SuppressWarnings("unchecked")
    public <V> V listGetIndex(@Nonnull String key, long index) {
        try {
            final var result = redisTemplate.opsForList()
                    .index(key, index);
            _assert(result);
            return (V) result;
        } catch (ClassCastException e) {
            throw e;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return 成功与否
     */
    public boolean listSet(@Nonnull String key, Object value) {
        try {
            final var result = redisTemplate.opsForList()
                    .rightPush(key, value);
            _assert(result);
            return result > 0;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return 成功与否
     */
    public boolean listSet(@Nonnull String key, Object value, long time) {
        if (!listSet(key, value)) {
            return false;
        }
        if (time > 0 && !expire(key, time)) {
            logger.warn("lSet(String,Object)中expire执行失败,key: {}", key);
        }
        return true;
    }

    /**
     * 将list放入缓存
     *
     * @param key    键
     * @param values 值
     * @return 成功与否
     */
    public boolean listSet(@Nonnull String key, List<Object> values) {
        try {
            final var result = redisTemplate.opsForList()
                    .rightPushAll(key, values);
            _assert(result);
            return result > 0;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key    键
     * @param values 值
     * @param time   时间(秒)
     * @return 成功与否
     */
    public boolean listSet(@Nonnull String key, List<Object> values, long time) {
        if (!listSet(key, values)) {
            return false;
        }
        if (time > 0 && !expire(key, time)) {
            logger.warn("lSet(String,List<Object>)中expire执行失败,key: {}", key);
        }
        return true;
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return 成功与否
     */
    public boolean listUpdateIndex(@Nonnull String key, long index, Object value) {
        try {
            redisTemplate.opsForList()
                    .set(key, index, value);
            return true;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long listRemove(@Nonnull String key, long count, Object value) {
        try {
            final var result = redisTemplate.opsForList()
                    .remove(key, count, value);
            _assert(result);
            return result;
        } catch (RuntimeException e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
    }
}
