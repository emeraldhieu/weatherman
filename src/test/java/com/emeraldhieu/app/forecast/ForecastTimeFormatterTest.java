package com.emeraldhieu.app.forecast;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ForecastTimeFormatterTest {

    private final ForecastTimeFormatter forecastTimeFormatter = new ForecastTimeFormatter();

    @Test
    public void givenForecastedTimeString_whenParse_thenReturnForecastedTime() {
        // GIVEN
        String forecastedTimeStr = "2022-11-05 12:00:00";
        LocalDateTime expectedForecastedTime = LocalDateTime.of(2022, 11, 5, 12, 0, 0);

        // WHEN
        LocalDateTime forecastedTime = forecastTimeFormatter.parse(forecastedTimeStr);

        // THEN
        assertEquals(expectedForecastedTime, forecastedTime);
    }
}