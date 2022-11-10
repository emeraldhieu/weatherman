package com.emeraldhieu.app.forecast.config;

import com.emeraldhieu.app.config.ForecastProperties;
import com.emeraldhieu.app.forecast.decoder.OriginalMessageErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class ForecastFeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor(ForecastProperties forecastProperties) {
        return template -> {
            template.query("appid", forecastProperties.getApiKey());
        };
    }

    /**
     * Feign's default error handler, ErrorDecoder.default, always throws a FeignException.
     * Configure an error encoder to customize the exception thrown.
     */
    @Bean
    public ErrorDecoder errorDecoder() {
        return new OriginalMessageErrorDecoder();
    }
}
