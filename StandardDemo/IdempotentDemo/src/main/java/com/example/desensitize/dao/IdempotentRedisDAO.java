package com.example.desensitize.dao;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 幂等 Redis DAO
 */
@Component
@AllArgsConstructor
public class IdempotentRedisDAO {
    
    /**
     * 幂等操作
     * <p>
     * KEY 格式：idempotent:%s // 参数为 uuid
     * VALUE 格式：String
     * 过期时间：不固定
     */
    private static final String IDEMPOTENT = "idempotent:%s";
    
    private final StringRedisTemplate redisTemplate;
    
    public Boolean setIfAbsent(String key, long timeout, TimeUnit timeUnit) {
        String redisKey = formatKey(key);
        return redisTemplate.opsForValue().setIfAbsent(redisKey, "", timeout, timeUnit);
    }
    
    private static String formatKey(String key) {
        return String.format(IDEMPOTENT, key);
    }
    
}
