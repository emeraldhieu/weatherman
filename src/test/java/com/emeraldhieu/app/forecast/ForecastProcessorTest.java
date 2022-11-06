package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.entity.Forecast;
import com.emeraldhieu.app.forecast.entity.ForecastDataItem;
import com.emeraldhieu.app.forecast.entity.Main;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ForecastProcessorTest {

    private Clock clock;
    private ForecastTimeFormatter forecastTimeFormatter;
    private ForecastProcessor forecastProcessor;

    @BeforeEach
    public void setUp() {
        clock = mock(Clock.class);
        forecastTimeFormatter = mock(ForecastTimeFormatter.class);
        forecastProcessor = new ForecastProcessor(clock, forecastTimeFormatter);
    }

    @Test
    public void givenHour0_whenGetCount_thenReturn8() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.parse("2022-11-05T00:00:42");
        when(clock.getCurrentTime()).thenReturn(dateTime);

        // WHEN
        int count = forecastProcessor.getTimestampCount();

        // THEN
        assertEquals(15, count);
    }

    @Test
    public void givenHour9_whenGetCount_thenReturn8() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.parse("2022-11-05T09:00:42");
        when(clock.getCurrentTime()).thenReturn(dateTime);

        // WHEN
        int count = forecastProcessor.getTimestampCount();

        // THEN
        assertEquals(12, count);
    }

    @Test
    public void givenHour18_whenGetCount_thenReturn9() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.parse("2022-11-05T18:00:42");
        when(clock.getCurrentTime()).thenReturn(dateTime);

        // WHEN
        int count = forecastProcessor.getTimestampCount();

        // THEN
        assertEquals(9, count);
    }

    @Test
    public void givenHour20_whenGetCount_thenReturn9() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.parse("2022-11-05T20:00:42");
        when(clock.getCurrentTime()).thenReturn(dateTime);

        // WHEN
        int count = forecastProcessor.getTimestampCount();

        // THEN
        assertEquals(9, count);
    }

    @Test
    public void givenHour21_whenGetCount_thenReturn8() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.parse("2022-11-05T21:00:42");
        when(clock.getCurrentTime()).thenReturn(dateTime);

        // WHEN
        int count = forecastProcessor.getTimestampCount();

        // THEN
        assertEquals(8, count);
    }

    @Test
    public void givenHighTemperature_whenIsWarm_thenWarm() {
        // GIVEN
        double temperature = 666;
        double minimumTemperature = 42;

        // WHEN
        boolean isWarm = forecastProcessor.isWarm(temperature, minimumTemperature);

        // THEN
        assertTrue(isWarm);
    }

    @Test
    public void givenLowTemperature_whenIsWarm_thenNotWarm() {
        // GIVEN
        double temperature = 18;
        double minimumTemperature = 42;

        // WHEN
        boolean isWarm = forecastProcessor.isWarm(temperature, minimumTemperature);

        // THEN
        assertFalse(isWarm);
    }

    @Test
    public void givenForecast_whenSkipTodayTimestamps_thenReturnForecastWithoutTodayTimestamps() {
        // GIVEN
        LocalDateTime currentTime = LocalDateTime.parse("2022-11-05T18:00:42");
        when(clock.getCurrentTime()).thenReturn(currentTime);

        String forecastedDateTimeStr1 = "2022-11-05T21:00:00";
        LocalDateTime forecastedDateTime1 = LocalDateTime.of(2022, 11, 5, 21, 0, 0);
        ForecastDataItem forecastDataItem1 = ForecastDataItem.builder()
            .main(Main.builder()
                .temp(42)
                .build())
            .forecastedTime(forecastedDateTimeStr1)
            .build();
        when(forecastTimeFormatter.parse(forecastedDateTimeStr1)).thenReturn(forecastedDateTime1);

        String forecastedDateTimeStr2 = "2022-11-06T00:00:00";
        LocalDateTime forecastedDateTime2 = LocalDateTime.of(2022, 11, 6, 0, 0, 0);
        ForecastDataItem forecastDataItem2 = ForecastDataItem.builder()
            .main(Main.builder()
                .temp(666)
                .build())
            .forecastedTime(forecastedDateTimeStr2)
            .build();
        when(forecastTimeFormatter.parse(forecastedDateTimeStr2)).thenReturn(forecastedDateTime2);

        Forecast forecast = Forecast.builder()
            .forecastDataItems(List.of(
                forecastDataItem1,
                forecastDataItem2
            ))
            .build();

        Forecast expectedForecast = Forecast.builder()
            .forecastDataItems(List.of(
                forecastDataItem2
            ))
            .build();

        // WHEN
        Forecast forecastWithoutTodayTimestamps = forecastProcessor.skipTodayTimestamps(forecast);

        // THEN
        assertEquals(expectedForecast, forecastWithoutTodayTimestamps);
    }
}