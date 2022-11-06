package com.emeraldhieu.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Forecast's properties that are configured under "forecast" node in the "application.yml" file.
 */
@ConfigurationProperties(prefix = "application.forecast")
@Data
public class ForecastProperties {

    private String apiKey;
    private String host;
    private String fiveDayForecastEndpoint;
}
