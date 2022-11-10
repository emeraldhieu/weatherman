package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.config.ForecastProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

/**
 * A component that rotates API key index to avoid exceeding API's rate limit.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyIndexRotator {

    private final ForecastProperties forecastProperties;
    private final Cache cache;
    private final String API_KEY_INDEX = "apiKeyIndex";

    public int rotateIndex() {
        int apiKeyIndex = getNextApiKeyIndex();
        if (apiKeyIndex >= forecastProperties.getApiKeys().size()) {
            apiKeyIndex = 0; // Back to index 0.
        }
        cache.put(API_KEY_INDEX, apiKeyIndex);
        return apiKeyIndex;
    }

    private int getNextApiKeyIndex() {
        if (cache.get(API_KEY_INDEX) == null) {
            return 0; // Default to 0 if not existed
        }
        return cache.get(API_KEY_INDEX, Integer.class) + 1;
    }
}
