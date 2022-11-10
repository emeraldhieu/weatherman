package com.emeraldhieu.app.ratelimit.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Rate limit's properties that are configured under "application.rateLimit" of "application.yml".
 */
@ConfigurationProperties(prefix = "application.api-rate-limit")
@Getter
@Setter
public class ApiRateLimitProperties {
    private String cacheKey;
    private int requestCount;
    private int durationInSeconds;
    private int expirationInSeconds;
}