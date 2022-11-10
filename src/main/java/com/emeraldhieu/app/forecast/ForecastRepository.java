package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.cacheentity.DayForecast;
import com.emeraldhieu.app.forecast.entity.Forecast;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A repository that wraps {@link RetryableForecastClient} to support caching.
 * If @Cacheable was applied on feign client, we wouldn't be able to mock feign's data and have the response cached.
 * See https://stackoverflow.com/questions/69282312/why-does-cachable-work-with-bean-return-mock-but-not-with-mockedbean#69284729
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ForecastRepository {

    private final Clock clock;
    private final Cache cache;
    private final ForecastProcessor forecastProcessor;
    private final ForecastTimeFormatter forecastTimeFormatter;
    private final RetryableForecastClient retryableForecastClient;
    private static final String PREFIX = "forecast";

    public DayForecast getDayForecast(String cityId, Unit unit) {
        LocalDate today = clock.getCurrentLocalDate();
        LocalDate tomorrow = today.plusDays(1);
        String cacheKey = getCacheKey(cityId, tomorrow);
        return getDayForecast(cityId, cacheKey, unit);
    }

    String getCacheKey(String cityId, LocalDate localDate) {
        String date = forecastTimeFormatter.format(localDate);
        return String.format("%s_%s_%s", PREFIX, cityId, date);
    }

    private DayForecast getDayForecast(String cityId, String cacheKey, Unit unit) {
        if (cache.get(cacheKey) == null) {
            Forecast forecast = retryableForecastClient.getForecast(cityId);
            List<DayForecast> dayForecasts = forecastProcessor.getDayForecasts(forecast);
            putCache(cityId, dayForecasts);
        }
        DayForecast cachedDayForecast = cache.get(cacheKey, DayForecast.class);
        return forecastProcessor.incorporateUnit(cachedDayForecast, unit);
    }

    private void putCache(String cityId, List<DayForecast> dayForecasts) {
        dayForecasts.forEach(dayForecast -> {
            String cacheKey = String.format("%s_%s_%s", PREFIX, cityId, dayForecast.getDate());
            if (cache.get(cacheKey) == null) {
                log.info(String.format("%s has been cached", cacheKey));
                cache.put(cacheKey, dayForecast);
            }
        });
    }

    List<DayForecast> getNextDayForecasts(String cityId) {
        int dayOffset = 1;
        int daysToRetrieve = 4;
        LocalDate today = clock.getCurrentLocalDate();
        return IntStream.rangeClosed(dayOffset, daysToRetrieve)
            .mapToObj(theDayOffset -> {
                LocalDate nextDay = today.plusDays(theDayOffset);
                String nextDayCacheKey = getCacheKey(cityId, nextDay);
                return getDayForecast(cityId, nextDayCacheKey, Unit.CELSIUS);
            })
            .collect(Collectors.toList());
    }
}
