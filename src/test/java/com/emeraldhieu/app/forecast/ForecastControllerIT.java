package com.emeraldhieu.app.forecast;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ForecastController.class})
class ForecastControllerIT {

    @MockBean
    private ForecastService forecastService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenGetCities_thenReturn200() throws Exception {
        // WHEN
        mockMvc.perform(get("/weather/summary")
                .param("unit", Unit.CELSIUS.getKeyword())
                .param("temperature", "42")
                .param("cities", "2618425", "3621849", "3133880")
            )
            .andExpect(status().isOk());

        // THEN
        verify(forecastService).getCities(Unit.CELSIUS, 42, List.of("2618425", "3621849", "3133880"));
    }

    @Test
    void whenGetTemperatures_thenReturn200() throws Exception {
        // WHEN
        mockMvc.perform(get("/weather/cities/{cityId}", "2618425"))
            .andExpect(status().isOk());

        // THEN
        verify(forecastService).getTemperatures("2618425");
    }
}