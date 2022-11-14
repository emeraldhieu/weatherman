package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.config.ForecastFeignConfiguration;
import com.emeraldhieu.app.forecast.entity.Forecast;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client that serves Open Weather API.
 */
@FeignClient(value = "forecast", url = "https://api.openweathermap.org",
    configuration = ForecastFeignConfiguration.class
)
public interface ForecastClient {

    @GetMapping(value = "/data/2.5/forecast")
    Forecast getForecast(@RequestParam("appid") String apiKey,
                         @RequestParam("id") String cityId,
                         @RequestParam("units") String unit,
                         @RequestParam("cnt") int count);
}
