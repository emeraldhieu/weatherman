package com.emeraldhieu.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redis's properties that are configured under "application.redis" of "application.yml".
 */
@ConfigurationProperties(prefix = "application.redis")
@Getter
@Setter
public class RedisProperties {
    private boolean enabled;
    private String[] servers;
    private String password;
    private int timeToLiveInMinutes;
    private int maxIdleTimeInMinutes;
    private String cachePrefix;
}