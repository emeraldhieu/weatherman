package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.entity.City;
import com.emeraldhieu.app.forecast.entity.Forecast;
import com.emeraldhieu.app.forecast.entity.ForecastDataItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Unit unit = Unit.CELSIUS;
        Forecast forecast = Forecast.builder()
            .forecastDataItems(List.of(
                ForecastDataItem.builder()
                    .forecastedTime("2022-11-05 18:00:00")
                    .build(),
                ForecastDataItem.builder()
                    .forecastedTime("2022-11-05 21:00:00")
                    .build()
            ))
            .city(City.builder()
                .id(cityId)
                .build())
            .build();
        when(forecastRepository.getForecast(cityId, unit.getApiUnit(), timestampCount)).thenReturn(forecast);

        when(forecastProcessor.skipTodayTimestamps(forecast))
            .thenReturn(Forecast.builder()
                .forecastDataItems(List.of(
                    ForecastDataItem.builder()
                        .forecastedTime("2022-11-05 21:00:00")
                        .build()
                ))
                .build());

        double averageTemperature = 9;
        CityResponse cityResponse = CityResponse.builder()
            .id(cityId)
            .averageTemperature(averageTemperature)
            .build();
        when(forecastMapper.getCityResponse(any())).thenReturn(cityResponse);

        double temperature = 42;
        when(forecastProcessor.isWarm(averageTemperature, temperature)).thenReturn(true);

        // WHEN
        List<CityResponse> cityResponses = forecastService.getCities(Unit.CELSIUS, temperature, List.of(cityId));

        // THEN
        assertEquals(cityResponse, cityResponses.get(0));
    }
}