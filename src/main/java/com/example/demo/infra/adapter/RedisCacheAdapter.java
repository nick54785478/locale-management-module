package com.example.demo.infra.adapter;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;

import com.example.demo.application.port.CacheMangerPort;
import com.example.demo.application.shared.dto.CacheGottenResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis 快取適配器 (作為備用方案)
 *
 * <p>
 * 使用 Spring Data Redis 的 RedisTemplate 來實作 CacheMangerPort 的契約。
 * </p>
 */
@Slf4j
@RequiredArgsConstructor
public class RedisCacheAdapter implements CacheMangerPort {

    private final RedisTemplate<String, Object> redisTemplate;

    private String buildKey(String cacheName, String key) {
        return cacheName + "::" + key;
    }

    @Override
    public void put(String cacheName, String key, Object value) {
        if (cacheName == null || key == null) return;
        redisTemplate.opsForValue().set(buildKey(cacheName, key), value);
    }

    @Override
    public void evict(String cacheName, String key) {
        if (cacheName == null || key == null) return;
        redisTemplate.delete(buildKey(cacheName, key));
    }

    @Override
    public void clear(String cacheName) {
        if (cacheName == null) return;
        Set<String> keys = redisTemplate.keys(cacheName + "::*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public Optional<Object> get(String cacheName, String key) {
        if (cacheName == null || key == null) return Optional.empty();
        Object value = redisTemplate.opsForValue().get(buildKey(cacheName, key));
        return Optional.ofNullable(value);
    }

    @Override
    public CacheGottenResult getAll(String cacheName) {
        CacheGottenResult result = new CacheGottenResult();
        result.setCacheName(cacheName);
        if (cacheName == null) return result;

        Set<String> keys = redisTemplate.keys(cacheName + "::*");
        if (keys != null) {
            for (String k : keys) {
                Object value = redisTemplate.opsForValue().get(k);
                // 移除 prefix "cacheName::" 取得原本的 key
                String originalKey = k.substring(cacheName.length() + 2);
                result.getDetails().add(new CacheGottenResult.CacheMetaData(originalKey, value));
            }
        }
        return result;
    }
}
