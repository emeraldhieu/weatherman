package com.emeraldhieu.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@EnableFeignClients
/**
 * Scan property classes annotated with {@link ConfigurationProperties}.
 * See https://www.baeldung.com/configuration-properties-in-spring-boot#1-spring-boot-22
 */
@ConfigurationPropertiesScan(basePackageClasses = {WeathermanApp.class})
@SpringBootApplication
@EnableRetry
public class WeathermanApp {

    public static void main(String[] args) {
        SpringApplication.run(WeathermanApp.class, args);
    }
}