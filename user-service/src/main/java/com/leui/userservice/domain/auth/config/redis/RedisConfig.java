package com.leui.userservice.domain.auth.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.RedisRepository;

@Configuration
public class RedisConfig {

    @Bean
    public RedisRepository repository(StringRedisTemplate template) {
        return new RedisRepository(template);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

}
