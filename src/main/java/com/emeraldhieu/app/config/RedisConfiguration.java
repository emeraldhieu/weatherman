package com.emeraldhieu.app.config;

import lombok.RequiredArgsConstructor;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnProperty(value = "application.redis.enabled", havingValue = "true")
@RequiredArgsConstructor
@Profile("!test")
public class RedisConfiguration {

    private final RedisProperties redisProperties;

    @Bean
    public Config config() {
        Config config = new Config();
        config.useSingleServer()
            .setAddress(redisProperties.getServers()[0])
            .setPassword(redisProperties.getPassword());
        return config;
    }
}