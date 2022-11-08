package com.emeraldhieu.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Rate limit's properties that are configured under "application.rateLimit" of "application.yml".
 */
@ConfigurationProperties(prefix = "application.rate-limit")
@Getter
@Setter
public class RateLimitProperties {
    private int requestCount;
    private int durationInSeconds;
    private int expirationInSeconds;
}