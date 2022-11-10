package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.config.ForecastProperties;
import com.emeraldhieu.app.forecast.entity.Forecast;
import com.emeraldhieu.app.forecast.exception.TooManyRequestException;
import com.emeraldhieu.app.ratelimit.api.ApiRateLimitProperties;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.context.MessageSource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * A wrapper of {@link ForecastClient} that supports rate-limiting retrying, and fallback.
 * If API quote is exceeded, the fallback will switch to the next API key.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RetryableForecastClient {

    private final ForecastClient forecastClient;
    private final ForecastProperties forecastProperties;
    private final Cache cache;
    private final ApiKeyIndexRotator apiKeyIndexRotator;
    private final ProxyManager apiProxyManager;
    private final BucketConfiguration apiBucketConfiguration;
    private final ApiRateLimitProperties apiRateLimitProperties;
    private static final Unit UNIT = Unit.CELSIUS;
    private static final int MAX_COUNT = 40;
    private final String API_KEY_INDEX = "apiKeyIndex";
    private final MessageSource messageSource;

    @Retryable(
        value = TooManyRequestException.class,
        maxAttemptsExpression = "${application.forecast.retry.maxAttempts}",
        backoff = @Backoff(delayExpression = "${application.forecast.retry.maxDelay}")
    )
    public Forecast getForecast(String cityId) {
        Bucket bucket = getBucket();

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        /**
         * Check if API rate limit is exceeded.
         */
        if (probe.isConsumed()) { // Rate limit is not exceeded

            if (cache.get(API_KEY_INDEX) == null) {
                cache.put(API_KEY_INDEX, 0);
            }

            int apiKeyIndex = cache.get(API_KEY_INDEX, Integer.class);
            return callForecastClient(cityId, apiKeyIndex);

        } else { // Rate limit is exceeded. Rotate the API key index.
            int nextApiKeyIndex = apiKeyIndexRotator.rotateIndex();

            log.info("{} has been exceeded. Switch API key index to {}.",
                messageSource.getMessage("rateLimitOneMillionCallsPerMonth", null, null),
                nextApiKeyIndex);

            Forecast forecast = callForecastClient(cityId, nextApiKeyIndex);

            // Refill the bucket for new rate limit.
            bucket.reset();

            return forecast;
        }
    }

    private Forecast callForecastClient(String cityId, int apiKeyIndex) {
        log.info("Call {} using API key index {}.", ForecastClient.class.getSimpleName(), apiKeyIndex);
        String apiKey = forecastProperties.getApiKeys().get(apiKeyIndex);
        return forecastClient.getForecast(apiKey, cityId, UNIT.getApiUnit(), MAX_COUNT);
    }

    @Recover
    public Forecast forecastFallback(TooManyRequestException exception, String cityId) {
        int nextApiKeyIndex = apiKeyIndexRotator.rotateIndex();

        log.info("{} has been exceeded. Switch API key index to {}.",
            messageSource.getMessage("rateLimitOneMillionCallsPerMonth", null, null),
            nextApiKeyIndex);

        String nextApiKey = forecastProperties.getApiKeys().get(nextApiKeyIndex);
        return forecastClient.getForecast(nextApiKey, cityId, UNIT.getApiUnit(), MAX_COUNT);
    }

    private Bucket getBucket() {
        String cacheKey = apiRateLimitProperties.getCacheKey();
        return apiProxyManager.builder()
            .build(cacheKey, apiBucketConfiguration);
    }
}
