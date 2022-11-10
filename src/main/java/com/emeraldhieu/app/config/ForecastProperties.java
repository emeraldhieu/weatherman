package com.emeraldhieu.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Forecast's properties that are configured under "application.forecast" of "application.yml".
 */
@ConfigurationProperties(prefix = "application.forecast")
@Getter
@Setter
public class ForecastProperties {

    private List<String> apiKeys;
    private String host;
    private String fiveDayForecastEndpoint;
    private String cacheName;
    private String cacheKeyPrefix;
    private int timeToLiveInMinutes;
    private int maxIdleTimeInMinutes;
    private String apiKeyIndexCacheKey;
}
