package com.transferencia.ByteBank.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean existeChave(String chave) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(chave));
    }

    public void salvarChaveComTTL(String chave, Duration ttl) {
        redisTemplate.opsForValue().set(chave, "true", ttl);
    }
}

