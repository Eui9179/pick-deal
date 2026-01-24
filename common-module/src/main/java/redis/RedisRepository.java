package redis;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOps;

    public RedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
    }

    public void put(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void put(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public String remove(String key) {
        return redisTemplate.opsForValue().getAndDelete(key);
    }

    public void remove(Set<String> keys) {
        redisTemplate.delete(keys);
    }

    public void zSetAdd(String key, String value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    public void zSetRemoveRange(String key, double start, double end) {
        redisTemplate.opsForZSet().removeRangeByScore(key, start, end - 1);
    }

    public void zSetRemove(String key, String value) {
        redisTemplate.opsForZSet().remove(key, value);
    }

    public void setTTL(String key, String value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public Set<String> zSetGetRange(String key, double start, double end) {
        return redisTemplate.opsForZSet().rangeByScore(key, start, end);
    }

    public Long zSetCountRange(String key, double start, double end) {
        return redisTemplate.opsForZSet().count(key, start, end);
    }

    public void hashPut(String key, String hashKey, String value) {
        hashOps.put(key, hashKey, value);
    }

    public Map<String, String> hashGetAll(String key) {
        return hashOps.entries(key);
    }

}
