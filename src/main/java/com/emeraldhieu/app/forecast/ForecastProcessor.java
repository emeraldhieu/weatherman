package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.entity.Forecast;
import com.emeraldhieu.app.forecast.entity.ForecastDataItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ForecastProcessor {

    private final Clock clock;
    private final ForecastTimeFormatter forecastTimeFormatter;

    /**
     * Get remaining timestamps of today plus 8 timestamps of the next day.
     * Since the external API doesn't support offset, we have to include remaining timestamps.
     */
    public int getTimestampCount() {
        int remainingParts = getRemainingTimestampsOfToday();
        int partsOfDay = 8; // 3-hour 8 parts a day.
        int count = remainingParts + partsOfDay;
        return count;
    }

    /**
     * Get remaining timestamps of today based on current time.
     * For example, if current time is 18:00, the only remaining timestamp is 21:00.
     */
    private int getRemainingTimestampsOfToday() {
        int currentHour = clock.getCurrentTime().getHour(); // e.g. 18
        int remainingHour = 24 - currentHour;
        int partsOfDay = 8; // 3-hour 8 parts a day.
        int remainingParts = (int) Math.ceil((double) remainingHour * partsOfDay / 24) - 1;
        return remainingParts;
    }

    public boolean isWarm(double averageTemperature, double minTemperature) {
        return averageTemperature > minTemperature;
    }

    public Forecast skipTodayTimestamps(Forecast forecast) {
        int today = clock.getCurrentTime().getDayOfMonth();
        List<ForecastDataItem> forecastDataItemsOfTomorrow = forecast.getForecastDataItems().stream()
            .filter(forecastDataItem -> {
                LocalDateTime forecastedTime = forecastTimeFormatter.parse(forecastDataItem.getForecastedTime());
                int forecastedDay = forecastedTime.getDayOfMonth();
                return forecastedDay > today;
            })
            .collect(Collectors.toList());
        return forecast.toBuilder()
            .forecastDataItems(forecastDataItemsOfTomorrow)
            .build();
    }
}
