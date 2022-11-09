package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.entity.Forecast;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultForecastService implements ForecastService {

    private final ForecastProcessor forecastProcessor;
    private final ForecastRepository forecastRepository;
    private final ForecastMapper forecastMapper;

    @Override
    public List<CityResponse> getCities(Unit unit, double temperature, List<String> cityIds) {
        int timestampCount = forecastProcessor.getTimestampCount();
        return cityIds.stream()
            .map(cityId -> forecastRepository.getForecast(cityId, unit.getApiUnit(), timestampCount))
            .map(forecastProcessor::skipTodayTimestamps)
            .map(forecastMapper::getCityResponse)
            .filter(forecastResponse -> forecastProcessor.isWarm(forecastResponse.getAverageTemperature(), temperature))
            .collect(Collectors.toList());
    }

    @Override
    public List<TemperatureResponse> getTemperatures(String cityId) {
        Forecast forecast = forecastRepository.getForecast(cityId);
        return forecastMapper.getTemperatureResponses(forecast);
    }
}
