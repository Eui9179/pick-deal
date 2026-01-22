package com.leui.orderservice.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class RedisRepository {

    private final StringRedisTemplate template;

    public void zSetAdd(String key, String value, double score) {
        template.opsForZSet().add(key, value, score);
    }

    public void zSetRemoveRange(String key, double start, double end) {
        template.opsForZSet().removeRangeByScore(key, start, end);
    }

    public void zSetRemove(String key, String value) {
        template.opsForZSet().remove(key, value);
    }

    public void set(String key, String value) {
        template.opsForValue().set(key, value);
    }

    public void setTTL(String key, String value, Duration duration) {
        template.opsForValue().set(key, value, duration);
    }

    public void get(String key) {
        template.opsForValue().get(key);
    }

}
