package com.emeraldhieu.app.config;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(value = "application.redis.enabled", havingValue = "true")
@RequiredArgsConstructor
@EnableCaching
public class RedisConfiguration {

    private final RedisProperties redisProperties;
    private final ForecastProperties forecastProperties;
    private final Environment environment;

    @Bean
    public Config config() {
        Config config = new Config();
        if (environment.acceptsProfiles(Profiles.of("test"))) {
            config.useSingleServer()
                .setAddress(redisProperties.getServers()[0]);
        } else {
            config.useSingleServer()
                .setAddress(redisProperties.getServers()[0])
                .setPassword(redisProperties.getPassword());
        }
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

    @Bean
    public Cache forecastCache(CacheManager cacheManager) {
        return cacheManager.getCache(forecastProperties.getCacheName());
    }

    public Map<String, CacheConfig> getCacheConfigMap() {
        Map<String, CacheConfig> config = new HashMap<>();
        long timeToLiveInMilliseconds = Duration.ofMinutes(forecastProperties.getTimeToLiveInMinutes()).toMillis();
        long maxIdleTimeInMilliseconds = Duration.ofMinutes(forecastProperties.getMaxIdleTimeInMinutes()).toMillis();
        config.put(forecastProperties.getCacheName(), new CacheConfig(timeToLiveInMilliseconds, maxIdleTimeInMilliseconds));
        return config;
    }
}