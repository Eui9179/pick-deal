package redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final ValueOperations<String, String> valueOperations;

    public RedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    public void put(String key, String value) {
        valueOperations.set(key, value);
    }

    public void putWithExpiration(String key, String value, Duration duration) {
        valueOperations.set(key, value);
        redisTemplate.expire(key, duration);
    }

    public String get(String key) {
        return valueOperations.get(key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public String delete(String key) {
        return valueOperations.getAndDelete(key);
    }

}
