package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.config.RedisConfiguration;
import com.emeraldhieu.app.forecast.entity.Forecast;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * The second way of using Open Feign without Spring.
 */
@FeignClient(value = "forecast", url = "https://api.openweathermap.org",
    configuration = ForecastFeignConfiguration.class
)
public interface ForecastClient {

    @GetMapping(value = "/data/2.5/forecast")
    @Cacheable(value = RedisConfiguration.CACHE_NAME, keyGenerator = "keyGenerator")
    Forecast getForecast(@RequestParam("id") String cityId, @RequestParam("units") String unit,
                         @RequestParam("cnt") int count);

    @GetMapping(value = "/data/2.5/forecast")
    @Cacheable(value = RedisConfiguration.CACHE_NAME, keyGenerator = "keyGenerator")
    Forecast getForecast(@RequestParam("id") String cityId);
}
