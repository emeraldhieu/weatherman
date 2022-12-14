package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.ratelimit.ip.IpRateLimitProperties;
import com.emeraldhieu.app.ratelimit.ip.IpThrottlingFilter;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import redis.embedded.RedisServer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class RateLimitIT {

    @Autowired
    private BucketConfiguration ipBucketConfiguration;

    @Autowired
    private ProxyManager<String> ipProxyManager;

    @Autowired
    private IpRateLimitProperties ipRateLimitProperties;

    @Autowired
    private ForecastController forecastController;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    private MockMvc mockMvc;

    private static RedisServer redisServer;

    @BeforeAll
    public static void setUpALl() {
        redisServer = new RedisServer();
        redisServer.start();
    }

    @AfterAll
    public static void tearDownAll() {
        redisServer.stop();
    }

    @BeforeEach
    public void setUp() {
        IpThrottlingFilter ipThrottlingFilter = new IpThrottlingFilter(ipProxyManager, ipBucketConfiguration,
            ipRateLimitProperties, exceptionResolver);
        this.mockMvc = MockMvcBuilders.standaloneSetup(forecastController).addFilters(ipThrottlingFilter).build();
    }

    @AfterEach
    public void destroy() {

    }

    @Test
    void givenRateLimitOneRequestPerTenSeconds_whenCallAnyEndpointForTheSecondTime_thenReturn429() throws Exception {
        // WHEN
        mockMvc.perform(get("/weather/summary")
                .param("unit", Unit.CELSIUS.getKeyword())
                .param("temperature", "42").param("cities", "2618425", "3621849", "3133880"))
            .andExpect(status().isOk());

        // THEN
        mockMvc.perform(get("/weather/summary")
                .param("unit", Unit.CELSIUS.getKeyword())
                .param("temperature", "42").param("cities", "2618425", "3621849", "3133880"))
            .andExpect(status().isTooManyRequests());

        mockMvc.perform(get("/weather/cities/{cityId}", "2618425"))
            .andExpect(status().isTooManyRequests());
    }
}