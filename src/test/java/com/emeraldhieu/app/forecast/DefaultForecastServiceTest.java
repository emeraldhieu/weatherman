package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.cacheentity.DayForecast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultForecastServiceTest {

    private ForecastProcessor forecastProcessor;
    private ForecastRepository forecastRepository;
    private ForecastMapper forecastMapper;
    private ForecastService forecastService;

    @BeforeEach
    public void setUp() {
        forecastProcessor = mock(ForecastProcessor.class);
        forecastRepository = mock(ForecastRepository.class);
        forecastMapper = mock(ForecastMapper.class);
        forecastService = new DefaultForecastService(forecastProcessor, forecastRepository, forecastMapper);
    }

    @Test
    public void givenCityIds_whenGetCities_thenReturnCityResponses() {
        // GIVEN
        int timestampCount = 9;
        when(forecastProcessor.getTimestampCount()).thenReturn(timestampCount);

        String cityId = "2618425";
        String name = "Copenhagen";
        LocalDate date = LocalDate.of(2022, 11, 5);
        double averageTemperature = 42;

        DayForecast dayForecast = DayForecast.builder()
            .id(cityId)
            .name(name)
            .averageTemperature(averageTemperature)
            .date(date)
            .build();

        when(forecastRepository.getDayForecast(cityId)).thenReturn(dayForecast);

        ForecastResponse expectedForecastResponse = ForecastResponse.builder()
            .id(cityId)
            .name(name)
            .averageTemperature(averageTemperature)
            .date(date)
            .build();
        when(forecastMapper.getForecastResponse(any())).thenReturn(expectedForecastResponse);

        double temperature = 42;
        when(forecastProcessor.isWarm(averageTemperature, temperature)).thenReturn(true);

        // WHEN
        List<ForecastResponse> forecastResponses = forecastService.getCities(Unit.CELSIUS, temperature, List.of(cityId));

        // THEN
        assertEquals(expectedForecastResponse, forecastResponses.get(0));
    }
}