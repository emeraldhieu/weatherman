package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.config.RedisConfiguration;
import com.emeraldhieu.app.forecast.cacheentity.DayForecast;
import com.emeraldhieu.app.forecast.entity.City;
import com.emeraldhieu.app.forecast.entity.Forecast;
import com.emeraldhieu.app.forecast.entity.ForecastDataItem;
import com.emeraldhieu.app.forecast.entity.Main;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import redis.embedded.RedisServer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class CacheIT {

    @MockBean
    private RetryableForecastClient retryableForecastClient;

    @Autowired
    private ForecastRepository forecastRepository;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private Clock clock;

    @Autowired
    private ForecastProcessor forecastProcessor;

    @Autowired
    private ForecastTimeFormatter forecastTimeFormatter;

    private static RedisServer redisServer;

    private String apiKey = "awesomeApiKey";
    private String cityId = "2618425";
    private String name = "Copenhagen";
    private LocalDate date = LocalDate.of(2022, 11, 6);
    private String apiUnit = Unit.CELSIUS.getApiUnit();
    private int maxCount = 40;
    private List<Integer> hours = List.of(0, 3, 6, 9, 12, 15, 18, 21);
    private Unit unit = Unit.CELSIUS;

    @BeforeAll
    public static void setUpALl() {
        redisServer = new RedisServer();
        redisServer.start();
    }

    @AfterAll
    public static void tearDownAll() {
        redisServer.stop();
    }

    @Test
    void givenCachedDayForecast_whenGetDayForecast_thenReturnCachedDayForecast() {
        // GIVEN
        LocalDate dateTime = LocalDate.of(2022, 11, 5);
        when(clock.getCurrentLocalDate()).thenReturn(dateTime);

        Forecast forecast = getForecast();
        given(retryableForecastClient.getForecast(cityId)).willReturn(forecast);

        DayForecast expectedDayForecast = getDayForecast();

        DayForecast dayForecastCacheMiss = forecastRepository.getDayForecast(cityId, unit);
        assertEquals(expectedDayForecast, dayForecastCacheMiss);

        // WHEN
        DayForecast dayForecastCacheHit = forecastRepository.getDayForecast(cityId, unit);
        assertEquals(expectedDayForecast, dayForecastCacheHit);

        // THEN
        verify(retryableForecastClient, times(1)).getForecast(cityId);

        Cache cache = cacheManager.getCache(RedisConfiguration.CACHE_NAME);
        String cacheKey = forecastRepository.getCacheKey(cityId, date);
        DayForecast cachedDayForecast = cache.get(cacheKey, DayForecast.class);
        assertEquals(expectedDayForecast, cachedDayForecast);
    }

    private DayForecast getDayForecast() {
        DayForecast expectedDayForecast = DayForecast.builder()
            .id(cityId)
            .name(name)
            .averageTemperature(hours.stream().mapToDouble(Integer::doubleValue).average().getAsDouble())
            .date(date)
            .build();
        return expectedDayForecast;
    }

    private Forecast getForecast() {

        List<ForecastDataItem> forecastDataItems = hours.stream()
            .map(hour -> {
                LocalDateTime dateTime = LocalDateTime.of(2022, 11, 6, hour, 0, 0);
                String dateTimeStr = forecastTimeFormatter.format(dateTime);
                return ForecastDataItem.builder()
                    .main(Main.builder()
                        .temp(hour)
                        .build())
                    .forecastedTime(dateTimeStr)
                    .build();
            })
            .collect(Collectors.toList());
        Forecast forecast = Forecast.builder()
            .forecastDataItems(forecastDataItems)
            .city(City.builder()
                .id(cityId)
                .name(name)
                .build())
            .build();
        return forecast;
    }

    @Test
    void givenCachedForecasts_whenGetNextDayForecastsByCityId_thenReturnCachedDayForecast() {
        // TODO Do it later. I'm tired.
    }
}