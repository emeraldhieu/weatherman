package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.entity.Forecast;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultForecastService implements ForecastService {

    private final ForecastClient forecastClient;
    private final ForecastMapper forecastMapper;
    private final ForecastProcessor forecastProcessor;

    @Override
    public List<CityResponse> getCities(Unit unit, double temperature, List<String> cityIds) {
        int timestampCount = forecastProcessor.getTimestampCount();
        return cityIds.stream()
            .map(cityId -> forecastClient.getForecast(cityId, unit.getApiUnit(), timestampCount))
            .map(forecastProcessor::skipTodayTimestamps)
            .map(forecastMapper::getCityResponse)
            .filter(forecastResponse -> forecastProcessor.isWarm(forecastResponse.getAverageTemperature(), temperature))
            .collect(Collectors.toList());
    }

    @Override
    public List<TemperatureResponse> getTemperatures(String cityId) {
        Forecast forecast = forecastClient.getForecast(cityId);
        return forecastMapper.getTemperatureResponses(forecast);
    }
}
