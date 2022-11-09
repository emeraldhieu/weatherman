package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.config.RedisConfiguration;
import com.emeraldhieu.app.forecast.entity.Forecast;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * A repository that wraps {@link ForecastClient} to support capabilities that require proxying such as caching.
 * If @Cacheable was applied on feign client, we wouldn't be able to mock feign's data and have the response cached.
 * See https://stackoverflow.com/questions/69282312/why-does-cachable-work-with-bean-return-mock-but-not-with-mockedbean#69284729
 */
@Repository
@RequiredArgsConstructor
public class ForecastRepository implements ForecastClient {

    private final ForecastClient forecastClient;

    @Override
    @Cacheable(value = RedisConfiguration.CACHE_NAME, keyGenerator = "keyGenerator")
    public Forecast getForecast(String cityId, String unit, int count) {
        return forecastClient.getForecast(cityId, unit, count);
    }

    @Override
    @Cacheable(value = RedisConfiguration.CACHE_NAME, keyGenerator = "keyGenerator")
    public Forecast getForecast(String cityId) {
        return forecastClient.getForecast(cityId);
    }
}
