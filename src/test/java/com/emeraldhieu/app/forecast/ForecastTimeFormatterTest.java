package com.emeraldhieu.app.forecast;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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

    @Test
    public void givenDateWithZeroPaddingDate_whenFormat_thenReturnDateStr() {
        // GIVEN
        LocalDate date = LocalDate.of(2022, 11, 5);
        String expectedDateStr = "2022-11-05";

        // WHEN
        String dateStr = forecastTimeFormatter.format(date);

        // THEN
        assertEquals(expectedDateStr, dateStr);
    }

    @Test
    public void givenDateWithoutZeroPaddingDate_whenFormat_thenReturnDateStr() {
        // GIVEN
        LocalDate date = LocalDate.of(2022, 11, 11);
        String expectedDateStr = "2022-11-11";

        // WHEN
        String dateStr = forecastTimeFormatter.format(date);

        // THEN
        assertEquals(expectedDateStr, dateStr);
    }

    @Test
    public void givenDateTimeWithZeroPaddingDate_whenFormat_thenReturnDateTimeStr() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.of(2022, 11, 5, 0, 0, 0);
        String expectedDateTimeStr = "2022-11-05 00:00:00";

        // WHEN
        String dateTimeStr = forecastTimeFormatter.format(dateTime);

        // THEN
        assertEquals(expectedDateTimeStr, dateTimeStr);
    }

    @Test
    public void givenDateTimeWithoutZeroPaddingDate_whenFormat_thenReturnDateTimeStr() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.of(2022, 11, 11, 0, 0, 0);
        String expectedDateTimeStr = "2022-11-11 00:00:00";

        // WHEN
        String dateTimeStr = forecastTimeFormatter.format(dateTime);

        // THEN
        assertEquals(expectedDateTimeStr, dateTimeStr);
    }
}