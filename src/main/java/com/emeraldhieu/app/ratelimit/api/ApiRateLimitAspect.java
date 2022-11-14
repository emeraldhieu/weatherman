package com.emeraldhieu.app.ratelimit.api;

import com.emeraldhieu.app.config.ForecastProperties;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.Cache;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * An aspect that checks API rate limiting using bucket4j.
 * See https://github.com/bucket4j/bucket4j
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiRateLimitAspect {

    private final Cache cache;
    private final ApiKeyIndexRotator apiKeyIndexRotator;
    private final ProxyManager apiProxyManager;
    private final BucketConfiguration apiBucketConfiguration;
    private final ApiRateLimitProperties apiRateLimitProperties;
    private final MessageSource messageSource;
    private final ForecastProperties forecastProperties;

    @Around("@annotation(ApiRateLimit)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Bucket bucket = getBucket();

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        /**
         * Check if API rate limit is exceeded.
         */
        if (probe.isConsumed()) { // Rate limit is not exceeded

            if (cache.get(forecastProperties.getApiKeyIndexCacheKey()) == null) {
                cache.put(forecastProperties.getApiKeyIndexCacheKey(), 0);
            }

            return joinPoint.proceed();

        } else { // Rate limit is exceeded. Rotate the API key index.
            int nextApiKeyIndex = apiKeyIndexRotator.rotateIndex();

            log.info("{} has been reached. Switch API key index to {}.",
                messageSource.getMessage("rateLimitOneMillionCallsPerMonth", null, null),
                nextApiKeyIndex);

            Object object = joinPoint.proceed();

            // Refill the bucket for new rate limit.
            bucket.reset();

            return object;
        }
    }

    private Bucket getBucket() {
        String cacheKey = apiRateLimitProperties.getCacheKey();
        return apiProxyManager.builder()
            .build(cacheKey, apiBucketConfiguration);
    }
}
