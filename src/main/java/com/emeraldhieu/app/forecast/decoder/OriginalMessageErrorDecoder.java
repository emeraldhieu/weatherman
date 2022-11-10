package com.emeraldhieu.app.forecast.decoder;

import com.emeraldhieu.app.forecast.exception.BadRequestException;
import com.emeraldhieu.app.forecast.exception.InternalServerErrorException;
import com.emeraldhieu.app.forecast.exception.NotFoundException;
import com.emeraldhieu.app.forecast.exception.TooManyRequestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Get the original message coming from feign client.
 * See https://www.baeldung.com/feign-retrieve-original-message
 */
public class OriginalMessageErrorDecoder implements ErrorDecoder {
    private ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        try (InputStream bodyInputStream = response.body().asInputStream()) {
            String bodyJson = new BufferedReader(
                new InputStreamReader(bodyInputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

            ObjectMapper mapper = new ObjectMapper();
            ExceptionMessage message = mapper.readValue(bodyJson, ExceptionMessage.class);

            if (response.status() == HttpStatus.BAD_REQUEST.value()) {
                return new BadRequestException(message.getMessage() != null
                    ? message.getMessage()
                    : HttpStatus.BAD_REQUEST.getReasonPhrase());
            } else if (response.status() == HttpStatus.NOT_FOUND.value()) {
                return new NotFoundException(message.getMessage() != null
                    ? message.getMessage()
                    : HttpStatus.NOT_FOUND.getReasonPhrase());
            } else if (response.status() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                return new TooManyRequestException(message.getMessage() != null
                    ? message.getMessage()
                    : HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
            } else if (response.status() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return new InternalServerErrorException(message.getMessage() != null
                    ? message.getMessage()
                    : HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            } else {
                return errorDecoder.decode(methodKey, response);
            }
        } catch (IOException e) {
            return new RuntimeException("Failed to get feign client's original message", e);
        }
    }
}