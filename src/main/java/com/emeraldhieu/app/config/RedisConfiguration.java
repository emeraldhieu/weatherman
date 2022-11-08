package com.emeraldhieu.app.config;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Profile("!test")
@Configuration
@ConditionalOnProperty(value = "application.redis.enabled", havingValue = "true")
@RequiredArgsConstructor
@EnableCaching
public class RedisConfiguration {

    private final RedisProperties redisProperties;
    public static final String CACHE_NAME = "forecast";

    @Bean
    public Config config() {
        Config config = new Config();
        config.useSingleServer()
            .setAddress(redisProperties.getServers()[0])
            .setPassword(redisProperties.getPassword());
        return config;
    }

    @Bean
    public RedissonClient redissonClient(Config config) {
        return Redisson.create(config);
    }

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = getCacheConfigMap();
        return new RedissonSpringCacheManager(redissonClient, config);
    }

    public Map<String, CacheConfig> getCacheConfigMap() {
        Map<String, CacheConfig> config = new HashMap<>();
        long timeToLiveInMilliseconds = Duration.ofMinutes(redisProperties.getTimeToLiveInMinutes()).toMillis();
        long maxIdleTimeInMilliseconds = Duration.ofMinutes(redisProperties.getMaxIdleTimeInMinutes()).toMillis();
        config.put(CACHE_NAME, new CacheConfig(timeToLiveInMilliseconds, maxIdleTimeInMilliseconds));
        return config;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) ->
            method.getName() + "_" +
                StringUtils.arrayToDelimitedString(params, "_");
    }
}