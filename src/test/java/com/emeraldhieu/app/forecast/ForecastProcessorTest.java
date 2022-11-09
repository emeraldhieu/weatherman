package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.cacheentity.DayForecast;
import com.emeraldhieu.app.forecast.entity.City;
import com.emeraldhieu.app.forecast.entity.Forecast;
import com.emeraldhieu.app.forecast.entity.ForecastDataItem;
import com.emeraldhieu.app.forecast.entity.Main;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ForecastProcessorTest {

    private Clock clock;
    private ForecastTimeFormatter forecastTimeFormatter;
    private ForecastProcessor forecastProcessor;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        clock = mock(Clock.class);
        forecastTimeFormatter = new ForecastTimeFormatter();
        forecastProcessor = new ForecastProcessor(clock, forecastTimeFormatter);
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void givenHour0_whenGetCount_thenReturn8() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.parse("2022-11-05T00:00:42");
        when(clock.getCurrentLocalDateTime()).thenReturn(dateTime);

        // WHEN
        int count = forecastProcessor.getTimestampCount();

        // THEN
        assertEquals(15, count);
    }

    @Test
    public void givenHour9_whenGetCount_thenReturn8() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.parse("2022-11-05T09:00:42");
        when(clock.getCurrentLocalDateTime()).thenReturn(dateTime);

        // WHEN
        int count = forecastProcessor.getTimestampCount();

        // THEN
        assertEquals(12, count);
    }

    @Test
    public void givenHour18_whenGetCount_thenReturn9() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.parse("2022-11-05T18:00:42");
        when(clock.getCurrentLocalDateTime()).thenReturn(dateTime);

        // WHEN
        int count = forecastProcessor.getTimestampCount();

        // THEN
        assertEquals(9, count);
    }

    @Test
    public void givenHour20_whenGetCount_thenReturn9() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.parse("2022-11-05T20:00:42");
        when(clock.getCurrentLocalDateTime()).thenReturn(dateTime);

        // WHEN
        int count = forecastProcessor.getTimestampCount();

        // THEN
        assertEquals(9, count);
    }

    @Test
    public void givenHour21_whenGetCount_thenReturn8() {
        // GIVEN
        LocalDateTime dateTime = LocalDateTime.parse("2022-11-05T21:00:42");
        when(clock.getCurrentLocalDateTime()).thenReturn(dateTime);

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
    public void givenForecast_whenGetDayForecasts_thenReturnValidDayForecasts() throws IOException {
        // GIVEN
        LocalDate currentLocalDate = LocalDate.parse("2022-11-04");
        when(clock.getCurrentLocalDate()).thenReturn(currentLocalDate);

        try (
            InputStream forecastInputStream = getClass().getClassLoader().getResourceAsStream("json/forecast.json");
            InputStream dayForecastInputStream = getClass().getClassLoader().getResourceAsStream("json/dayForecasts.json")
        ) {
            Forecast forecast = objectMapper.readValue(forecastInputStream, Forecast.class);

            CollectionType dayForecastCollectionType = TypeFactory.defaultInstance().constructCollectionType(List.class, DayForecast.class);
            List<DayForecast> expectedDayForecasts = objectMapper.readValue(dayForecastInputStream, dayForecastCollectionType);

            // WHEN
            List<DayForecast> dayForecasts = forecastProcessor.getDayForecasts(forecast);

            // THEN
            assertEquals(expectedDayForecasts, dayForecasts);
        }
    }

    @Test
    public void givenDateAfterCurrentDate_whenIsValidDayForecast_thenReturnTrue() {
        // GIVEN
        LocalDate currentDate = LocalDate.parse("2022-11-04");
        LocalDate date = LocalDate.parse("2022-11-05");

        List<ForecastDataItem> forecastDataItems = List.of(
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build()
        );

        // WHEN
        boolean isValidForecast = forecastProcessor.isValidDayForecast(currentDate, date, forecastDataItems);

        // THEN
        assertTrue(isValidForecast);
    }

    @Test
    public void givenDateBeforeCurrentDate_whenIsValidDayForecast_thenReturnTrue() {
        // GIVEN
        LocalDate currentDate = LocalDate.parse("2022-11-04");
        LocalDate date = LocalDate.parse("2022-11-03");

        List<ForecastDataItem> forecastDataItems = List.of(
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build(),
            ForecastDataItem.builder().build()
        );

        // WHEN
        boolean isValidForecast = forecastProcessor.isValidDayForecast(currentDate, date, forecastDataItems);

        // THEN
        assertFalse(isValidForecast);
    }

    @Test
    public void givenInsufficientForecastDataItems_whenIsValidDayForecast_thenReturnFalse() {
        // GIVEN
        LocalDate currentDate = LocalDate.parse("2022-11-04");
        LocalDate date = LocalDate.parse("2022-11-05");

        List<ForecastDataItem> forecastDataItems = List.of(
            ForecastDataItem.builder().build()
        );

        // WHEN
        boolean isValidForecast = forecastProcessor.isValidDayForecast(currentDate, date, forecastDataItems);

        // THEN
        assertFalse(isValidForecast);
    }

    @Test
    public void givenInsufficientForecastDataItems_whenGetDayForecast_thenReturnFalse() {
        // GIVEN
        String cityId = "2618425";
        String name = "Copenhagen";
        double temperature = 42;

        City city = City.builder()
            .id(cityId)
            .name(name)
            .build();

        List<ForecastDataItem> forecastDataItems = List.of(
            ForecastDataItem.builder()
                .main(Main.builder()
                    .temp(temperature)
                    .build())
                .build()
        );

        LocalDate date = LocalDate.parse("2022-11-05");

        // WHEN
        DayForecast dayForecast = forecastProcessor.incorporateUnit(city, date, forecastDataItems);

        // THEN
        assertEquals(city.getId(), dayForecast.getId());
        assertEquals(city.getName(), dayForecast.getName());
        assertEquals(forecastProcessor.getAverageTemperature(forecastDataItems), dayForecast.getAverageTemperature());
        assertEquals(date, dayForecast.getDate());
    }
}