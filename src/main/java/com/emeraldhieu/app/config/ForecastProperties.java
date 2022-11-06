package com.emeraldhieu.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Forecast's properties that are configured under "application.forecast" of "application.yml".
 */
@ConfigurationProperties(prefix = "application.forecast")
@Getter
@Setter
public class ForecastProperties {

    private String apiKey;
    private String host;
    private String fiveDayForecastEndpoint;
}
