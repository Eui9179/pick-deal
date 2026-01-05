package redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

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

    public void putWithExpirationDays(String key, String value, long expire) {
        putWithExpiration(key, value, expire, TimeUnit.DAYS);
    }

    public void putWithExpirationHours(String key, String value, long expire) {
        putWithExpiration(key, value, expire, TimeUnit.HOURS);
    }

    public void putWithExpirationMinutes(String key, String value, long expire) {
        putWithExpiration(key, value, expire, TimeUnit.MINUTES);
    }

    public void putWithExpirationSeconds(String key, String value, long expire) {
        putWithExpiration(key, value, expire, TimeUnit.SECONDS);
    }

    public void putWithExpiration(String key, String value, long expire, TimeUnit timeUnit) {
        valueOperations.set(key, value);
        redisTemplate.expire(key, expire, timeUnit);
    }

    public String get(String key) {
        return valueOperations.get(key);
    }

    public String delete(String key) {
        return valueOperations.getAndDelete(key);
    }

}
