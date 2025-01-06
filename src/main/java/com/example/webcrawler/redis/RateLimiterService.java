package com.example.webcrawler.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private final Random random;

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.random = new Random();
    }

    public boolean isAllowed(String domain, int maxRequestsPerSecond) {
        String key = "rate_limit:" + domain;
        long now = System.currentTimeMillis();
        long windowStart = now - 1000; // Sliding window: last 1 second

        // Clean up old requests
        redisTemplate.opsForZSet().removeRangeByScore(key, 0, windowStart);

        // Check current request count in the sliding window
        Long currentCount = redisTemplate.opsForZSet().size(key);

        if (currentCount != null && currentCount >= maxRequestsPerSecond) {
            return false;
        }

        // Add the current timestamp to the sliding window
        redisTemplate.opsForZSet().add(key, String.valueOf(now), now);
        redisTemplate.expire(key, 1, TimeUnit.SECONDS);

        return true;
    }

    public void applyJitter() {
        // Add a random delay between 100ms to 300ms
        try {
            Thread.sleep(100 + random.nextInt(200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during jitter", e);
        }
    }
}
