package com.emeraldhieu.app.config;

import lombok.RequiredArgsConstructor;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TestRedisConfiguration {

    private final RedisProperties redisProperties;

    @Bean
    public Config config() {
        Config config = new Config();
        config.useSingleServer()
            .setAddress(redisProperties.getServers()[0]);
        // No password required.
        return config;
    }
}