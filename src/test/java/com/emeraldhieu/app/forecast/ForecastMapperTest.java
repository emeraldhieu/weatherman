package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.entity.City;
import com.emeraldhieu.app.forecast.entity.Forecast;
import com.emeraldhieu.app.forecast.entity.ForecastDataItem;
import com.emeraldhieu.app.forecast.entity.Main;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ForecastMapperTest {

    private ForecastTimeFormatter forecastTimeFormatter;
    private ForecastMapper forecastMapper;

    @BeforeEach
    public void setUp() {
        forecastTimeFormatter = mock(ForecastTimeFormatter.class);
        forecastMapper = new ForecastMapper(forecastTimeFormatter);
    }

    @Test
    public void givenForecasts_whenGetCityResponses_thenReturnCityResponses() {
        // GIVEN
        String cityId = "awesomeCityId";
        String cityName = "Awesome city";
        double temp1 = 42;
        double temp2 = 666;
        List<Forecast> forecasts = List.of(
            Forecast.builder()
                .city(City.builder()
                    .id(cityId)
                    .name(cityName)
                    .build())
                .forecastDataItems(List.of(
                    ForecastDataItem.builder()
                        .main(Main.builder()
                            .temp(temp1)
                            .build())
                        .build(),
                    ForecastDataItem.builder()
                        .main(Main.builder()
                            .temp(temp2)
                            .build())
                        .build()
                ))
                .build()
        );

        // WHEN
        List<CityResponse> cityResponses = forecastMapper.getCityResponses(forecasts);

        // THEN
        assertEquals(cityId, cityResponses.get(0).getId());
        assertEquals(cityName, cityResponses.get(0).getName());
        assertEquals((temp1 + temp2) / 2, cityResponses.get(0).getAverageTemperature());
    }

    @Test
    public void givenForecastDataItems_whenGetAverageTemperature_thenReturnCeilingRoundedAverageTemperature() {
        // GIVEN
        double temp1 = 1;
        double temp2 = 2;
        double temp3 = 4;
        List<ForecastDataItem> forecastDataItems = List.of(
            ForecastDataItem.builder()
                .main(Main.builder()
                    .temp(temp1)
                    .build())
                .build(),
            ForecastDataItem.builder()
                .main(Main.builder()
                    .temp(temp2)
                    .build())
                .build(),
            ForecastDataItem.builder()
                .main(Main.builder()
                    .temp(temp3)
                    .build())
                .build()
        );

        // WHEN
        double averageTemperature = forecastMapper.getAverageTemperature(forecastDataItems);

        // THEN
        assertEquals(2.34, averageTemperature);
    }

    @Test
    public void givenForecastDataItems_whenGetTemperatureResponse_thenReturnTemperatureResponse() {
        // GIVEN
        String forecastedTimeStr1 = "2022-11-05 12:00:00";
        double temperature1 = 42;
        ForecastDataItem forecastDataItem1 = getForecastDataItem(forecastedTimeStr1, temperature1);
        LocalDateTime dateTime1 = LocalDateTime.of(2022, 11, 5, 12, 0, 0);
        mockForecastTimeFormatter(forecastedTimeStr1, dateTime1);

        String forecastedTimeStr2 = "2022-11-05 15:00:00";
        double temperature2 = 666;
        ForecastDataItem forecastDataItem2 = getForecastDataItem(forecastedTimeStr2, temperature2);
        LocalDateTime dateTime2 = LocalDateTime.of(2022, 11, 5, 15, 0, 0);
        mockForecastTimeFormatter(forecastedTimeStr2, dateTime2);

        String forecastedTimeStr3 = "2022-11-06 06:00:00";
        double temperature3 = 18;
        ForecastDataItem forecastDataItem3 = getForecastDataItem(forecastedTimeStr3, temperature3);
        LocalDateTime dateTime3 = LocalDateTime.of(2022, 11, 6, 6, 0, 0);
        mockForecastTimeFormatter(forecastedTimeStr3, dateTime3);

        String forecastedTimeStr4 = "2022-11-06 09:00:00";
        double temperature4 = 17;
        ForecastDataItem forecastDataItem4 = getForecastDataItem(forecastedTimeStr4, temperature4);
        LocalDateTime dateTime4 = LocalDateTime.of(2022, 11, 6, 9, 0, 0);
        mockForecastTimeFormatter(forecastedTimeStr4, dateTime4);

        Forecast forecast = Forecast.builder()
            .forecastDataItems(List.of(
                forecastDataItem1,
                forecastDataItem2,
                forecastDataItem3,
                forecastDataItem4
            ))
            .build();

        List<TemperatureResponse> expectedTemperatureResponses = List.of(
            TemperatureResponse.builder()
                .averageTemperature((temperature1 + temperature2) / 2)
                .date(dateTime1.toLocalDate())
                .build(),
            TemperatureResponse.builder()
                .averageTemperature((temperature3 + temperature4) / 2)
                .date(dateTime3.toLocalDate())
                .build()
        );

        // WHEN
        List<TemperatureResponse> temperatureResponse = forecastMapper.getTemperatureResponses(forecast);

        // THEN
        assertEquals(expectedTemperatureResponses, temperatureResponse);
    }

    private ForecastDataItem getForecastDataItem(String dateTimeStr, double temperature) {
        return ForecastDataItem.builder()
            .main(Main.builder()
                .temp(temperature)
                .build())
            .forecastedTime(dateTimeStr)
            .build();
    }

    private void mockForecastTimeFormatter(String dateTimeStr, LocalDateTime dateTime) {
        when(forecastTimeFormatter.parse(dateTimeStr)).thenReturn(dateTime);
    }
}