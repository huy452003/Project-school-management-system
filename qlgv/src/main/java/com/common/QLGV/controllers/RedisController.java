package com.common.QLGV.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/redis")
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/keys")
    public List<String> getAllKeys() {
        Set<String> keys = redisTemplate.keys("*");
        return keys != null ? new ArrayList<>(keys) : new ArrayList<>();
    }

    @GetMapping("/keys/{pattern}")
    public List<String> getKeysByPattern(@PathVariable String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        return keys != null ? new ArrayList<>(keys) : new ArrayList<>();
    }

    @GetMapping("/value/{key}")
    public Object getValue(@PathVariable String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @GetMapping("/hash/{key}")
    public Map<Object, Object> getHash(@PathVariable String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @GetMapping("/set/{key}")
    public Set<Object> getSet(@PathVariable String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @GetMapping("/list/{key}")
    public List<Object> getList(@PathVariable String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    @GetMapping("/zset/{key}")
    public Set<Object> getZSet(@PathVariable String key) {
        return redisTemplate.opsForZSet().range(key, 0, -1);
    }

    @GetMapping("/type/{key}")
    public String getKeyType(@PathVariable String key) {
        return redisTemplate.type(key).name();
    }

    @GetMapping("/ttl/{key}")
    public Long getTTL(@PathVariable String key) {
        return redisTemplate.getExpire(key);
    }

    @GetMapping("/info")
    public Map<String, Object> getRedisInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("totalKeys", redisTemplate.keys("*").size());
        info.put("connection", "localhost:6379");
        return info;
    }

    @DeleteMapping("/key/{key}")
    public Boolean deleteKey(@PathVariable String key) {
        return redisTemplate.delete(key);
    }

    @DeleteMapping("/keys")
    public Long deleteAllKeys() {
        Set<String> keys = redisTemplate.keys("*");
        return keys != null ? redisTemplate.delete(keys) : 0L;
    }
} 