package com.emeraldhieu.app.forecast;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.beans.PropertyEditorSupport;
import java.util.List;

@RestController
@RequestMapping("weather")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    @GetMapping("/summary")
    @ResponseStatus(HttpStatus.OK)
    public List<ForecastResponse> getCities(@RequestParam Unit unit, @RequestParam Double temperature,
                                            @RequestParam(value = "cities", defaultValue = "") List<String> cityIds) {
        return forecastService.getCities(unit, temperature, cityIds);
    }

    /**
     * Convert string request param to enum.
     * See https://stackoverflow.com/questions/39774427/springs-requestparam-with-enum#39774853
     */
    @InitBinder
    public void initBinder(WebDataBinder webdataBinder) {
        webdataBinder.registerCustomEditor(Unit.class, new PropertyEditorSupport() {
            public void setAsText(String text) {
                setValue(Unit.forKeyword(text));
            }
        });
    }

    @GetMapping("/cities/{cityId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ForecastResponse> getTemperatures(@PathVariable String cityId) {
        return forecastService.getTemperatures(cityId);
    }
}
