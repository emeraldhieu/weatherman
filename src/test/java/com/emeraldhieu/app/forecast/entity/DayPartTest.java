package com.emeraldhieu.app.forecast.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DayPartTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void givenDay_whenSerialize_thenReturnJson() throws JsonProcessingException {
        // GIVEN
        TestObject testObject = TestObject.builder()
            .dayPart(DayPart.DAY)
            .build();

        // WHEN
        String json = objectMapper.writeValueAsString(testObject);

        // THEN
        assertEquals("{\"dayPart\":\"d\"}", json);
    }

    @Test
    public void givenJson_whenDeserialize_thenReturnDay() throws JsonProcessingException {
        // GIVEN
        String json = "{\"dayPart\": \"d\"}";

        // WHEN
        TestObject testObject = objectMapper.readValue(json, TestObject.class);

        // THEN
        assertEquals(DayPart.DAY, testObject.getDayPart());
    }

    @Test
    public void givenJson_whenDeserialize_thenThrowsInvalidFormatException() {
        // GIVEN
        String json = "{\"dayPart\": \"invalid\"}";

        // WHEN and THEN
        assertThrows(InvalidFormatException.class,
            () -> objectMapper.readValue(json, TestObject.class));
    }

    @Builder(toBuilder = true)
    @Getter
    @Jacksonized
    private static class TestObject {
        private final DayPart dayPart;
    }
}