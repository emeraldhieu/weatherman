package com.emeraldhieu.app.ratelimit.ip;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Rate limit's properties that are configured under "application.rateLimit" of "application.yml".
 */
@ConfigurationProperties(prefix = "application.ip-rate-limit")
@Getter
@Setter
public class IpRateLimitProperties {
    private String namespace;
    private int requestCount;
    private int durationInSeconds;
    private int expirationInSeconds;
}