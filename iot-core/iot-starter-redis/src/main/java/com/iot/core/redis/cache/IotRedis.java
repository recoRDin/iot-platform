package com.iot.core.redis.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Objects;

public class IotRedis {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public IotRedis(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {

        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;

    }

    //写入永久缓存
    public void set(String key, Object value) {
        checkKey(key);
        Objects.requireNonNull(value,"缓存值不能为空");

        redisTemplate.opsForValue().set(key, value);
    }

    //写入带过期时间的缓存
    public void set(String key, Object value, Duration timeout) {
        checkKey(key);
        Objects.requireNonNull(value,"缓存值不能为空");
        checkTimeout(timeout);

        redisTemplate.opsForValue().set(key, value, timeout);
    }

    //读取缓存并转换成指定类型
    public <T> T get(String key, Class<T> targetType) {
        checkKey(key);
        Objects.requireNonNull(targetType, "目标类型不能为空");

        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        if (targetType.isInstance(value)) {
            return targetType.cast(value);
        }

        return objectMapper.convertValue(value, targetType);
    }

    //删除缓存
    public boolean delete(String key) {
        checkKey(key);
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    //判断缓存是否存在
    public boolean hasKey(String key) {
        checkKey(key);
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    //设置或更新缓存过期时间
    public boolean expire(String key, Duration timeout) {
        checkKey(key);
        checkTimeout(timeout);

        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout));
    }

    // 查询缓存剩余过期时间
    public Long getExpireSeconds(String key) {
        checkKey(key);
        return redisTemplate.getExpire(key);
    }

    // Key 不存在时才写入，并设置过期时间
    public boolean setIfAbsent(String key, Object value, Duration timeout) {

        checkKey(key);
        Objects.requireNonNull(value, "缓存值不能为空");
        checkTimeout(timeout);

        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(key, value, timeout)
        );
    }

    //判断缓存是否为空
    private void checkKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("缓存Key不能为空");
        }
    }
    //检查过期时间
    private void checkTimeout(Duration timeout) {
        if (timeout == null || timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("缓存过期时间必须大于0");
        }
    }
}
