package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.config.RedisConfiguration;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class CacheIT {

    @MockBean
    private ForecastClient forecastClient;

    @Autowired
    private ForecastRepository forecastRepository;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private Clock clock;

    @Autowired
    private ForecastProcessor forecastProcessor;

    private static RedisServer redisServer;

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
    void givenCachedForecast_whenGetForecast_thenReturnCachedForecast() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.parse("2022-11-05T18:00:42");
        when(clock.getCurrentTime()).thenReturn(dateTime);

        String cityId = "2618425";
        String apiUnit = Unit.CELSIUS.getApiUnit();
        int count = forecastProcessor.getTimestampCount();
        Forecast forecast = getForecast(cityId);
        given(forecastClient.getForecast(cityId, apiUnit, count)).willReturn(forecast);

        Forecast forecastCacheMiss = forecastRepository.getForecast(cityId, apiUnit, count);
        assertEquals(forecast, forecastCacheMiss);

        // WHEN
        Forecast forecastCacheHit = forecastRepository.getForecast(cityId, apiUnit, count);
        assertEquals(forecast, forecastCacheHit);

        // THEN
        verify(forecastClient, times(1)).getForecast(cityId, apiUnit, count);

        Cache cache = cacheManager.getCache(RedisConfiguration.CACHE_NAME);
        String keyOutside = String.format("getForecast_%s_%s_%d", cityId, apiUnit, count);
        Forecast cachedForecast = cache.get(keyOutside, Forecast.class);
        assertEquals(forecast, cachedForecast);
    }

    private static Forecast getForecast(String cityId) {
        Forecast forecast = Forecast.builder()
            .forecastDataItems(List.of(
                ForecastDataItem.builder()
                    .main(Main.builder()
                        .temp(42)
                        .build())
                    .forecastedTime("2022-11-06 06:00:00")
                    .build()
            ))
            .city(City.builder()
                .id(cityId)
                .build())
            .build();
        return forecast;
    }

    @Test
    void givenCachedForecast_whenGetForecastByCityId_thenReturnCachedForecast() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.parse("2022-11-05T18:00:42");
        when(clock.getCurrentTime()).thenReturn(dateTime);

        String cityId = "2618425";
        Forecast forecast = getForecast(cityId);
        given(forecastClient.getForecast(cityId)).willReturn(forecast);

        Forecast forecastCacheMiss = forecastRepository.getForecast(cityId);
        assertEquals(forecast, forecastCacheMiss);

        // WHEN
        Forecast forecastCacheHit = forecastRepository.getForecast(cityId);
        assertEquals(forecast, forecastCacheHit);

        // THEN
        verify(forecastClient, times(1)).getForecast(cityId);

        Cache cache = cacheManager.getCache(RedisConfiguration.CACHE_NAME);
        String keyOutside = String.format("getForecast_%s", cityId);
        Forecast cachedForecast = cache.get(keyOutside, Forecast.class);
        assertEquals(forecast, cachedForecast);
    }
}