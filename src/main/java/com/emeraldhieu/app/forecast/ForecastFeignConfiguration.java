package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.config.ForecastProperties;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class ForecastFeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor(ForecastProperties forecastProperties) {
        return template -> {
            template.query("appid", forecastProperties.getApiKey());
        };
    }
}
