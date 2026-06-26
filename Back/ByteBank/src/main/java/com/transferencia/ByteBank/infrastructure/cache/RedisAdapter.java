package com.transferencia.ByteBank.infrastructure.cache;

import com.transferencia.ByteBank.domain.port.out.TransferenciaCachePort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisAdapter implements TransferenciaCachePort {

    private final StringRedisTemplate redisTemplate;

    public RedisAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean existeChave(String chave) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(chave));
    }

    @Override
    public void salvarComTTL(String chave, Duration ttl) {
        redisTemplate.opsForValue().set(chave, "true", ttl);
    }
}
