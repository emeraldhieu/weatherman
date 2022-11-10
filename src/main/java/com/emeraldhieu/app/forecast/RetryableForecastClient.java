package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.config.ForecastProperties;
import com.emeraldhieu.app.forecast.entity.Forecast;
import com.emeraldhieu.app.forecast.exception.TooManyRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * A wrapper of {@link ForecastClient} that supports retrying and fallback.
 * If API quote is exceeded, the fallback will switch to the next API key.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RetryableForecastClient {

    private final ForecastClient forecastClient;
    private static final Unit UNIT = Unit.CELSIUS;
    private static final int MAX_COUNT = 40;
    private final ForecastProperties forecastProperties;
    private final Cache cache;
    private final String API_KEY_INDEX = "apiKeyIndex";

    @Retryable(
        value = TooManyRequestException.class,
        maxAttemptsExpression = "${application.forecast.retry.maxAttempts}",
        backoff = @Backoff(delayExpression = "${application.forecast.retry.maxDelay}")
    )
    public Forecast getForecast(String cityId) {
        log.info("Retrying #getForecast...");
        if (cache.get(API_KEY_INDEX) == null) {
            cache.put(API_KEY_INDEX, 0);
        }
        Integer apiKeyIndex = cache.get(API_KEY_INDEX, Integer.class);
        String apiKey = forecastProperties.getApiKeys().get(apiKeyIndex);
        return forecastClient.getForecast(apiKey, cityId, UNIT.getApiUnit(), MAX_COUNT);
    }

    @Recover
    public Forecast forecastFallback(TooManyRequestException exception, String cityId) {
        log.info("End of quota. Switching to the next api key...");
        int nextApiKeyIndex = getNextApiKeyIndex();
        if (nextApiKeyIndex >= forecastProperties.getApiKeys().size()) {
            throw exception; // Sorry, can't help
        }
        cache.put(API_KEY_INDEX, nextApiKeyIndex);
        String nextApiKey = forecastProperties.getApiKeys().get(nextApiKeyIndex);
        return forecastClient.getForecast(nextApiKey, cityId, UNIT.getApiUnit(), MAX_COUNT);
    }

    private int getNextApiKeyIndex() {
        int apiKeyIndex = cache.get(API_KEY_INDEX, Integer.class);
        return apiKeyIndex + 1;
    }
}
