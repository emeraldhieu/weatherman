package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.cacheentity.DayForecast;
import com.emeraldhieu.app.forecast.entity.ForecastDataItem;
import com.emeraldhieu.app.forecast.entity.Main;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ForecastMapperTest {

    private ForecastMapper forecastMapper;

    @BeforeEach
    public void setUp() {
        forecastMapper = new ForecastMapper();
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
    public void givenForecastDataItems_whenGetForecastResponse_thenReturnForecastResponse() {
        // GIVEN
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

        ForecastResponse expectedForecastResponse = ForecastResponse.builder()
            .id(cityId)
            .name(name)
            .averageTemperature(averageTemperature)
            .date(date)
            .build();

        // WHEN
        ForecastResponse forecastResponse = forecastMapper.getForecastResponse(dayForecast);

        // THEN
        assertEquals(expectedForecastResponse, forecastResponse);
    }
}