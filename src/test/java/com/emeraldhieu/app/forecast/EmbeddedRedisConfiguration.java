package com.emeraldhieu.app.forecast;

import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * An embedded Redis configuration for integration test.
 * See https://github.com/kstyrc/embedded-redis#usage
 */
@Configuration
public class EmbeddedRedisConfiguration {

    private RedisServer redisServer;

    public EmbeddedRedisConfiguration() {
        this.redisServer = new RedisServer();
    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}