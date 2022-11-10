package com.emeraldhieu.app.config;

import com.emeraldhieu.app.ratelimit.ip.IpRateLimitProperties;
import com.emeraldhieu.app.ratelimit.ip.IpThrottlingFilter;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@RequiredArgsConstructor
public class FilterConfiguration {

    private final ProxyManager<String> ipProxyManager;
    private final BucketConfiguration ipBucketConfiguration;
    private final IpRateLimitProperties ipRateLimitProperties;

    /**
     * Used to handle exceptions in filters.
     * See https://stackoverflow.com/questions/34595605/how-to-manage-exceptions-thrown-in-filters-in-spring#34595605
     */
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    @Bean
    public FilterRegistrationBean<IpThrottlingFilter> ipThrottlingFilter() {
        FilterRegistrationBean<IpThrottlingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new IpThrottlingFilter(ipProxyManager, ipBucketConfiguration, ipRateLimitProperties, exceptionResolver));
        registrationBean.addUrlPatterns("/weather/summary");
        registrationBean.addUrlPatterns("/weather/cities/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
        return registrationBean;
    }
}
