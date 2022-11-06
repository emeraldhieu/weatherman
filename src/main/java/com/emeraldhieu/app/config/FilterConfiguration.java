package com.emeraldhieu.app.config;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@RequiredArgsConstructor
public class FilterConfiguration {

    private final ProxyManager<String> proxyManager;

    @Bean
    public FilterRegistrationBean<IpThrottlingFilter> ipThrottlingFilter() {
        FilterRegistrationBean<IpThrottlingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new IpThrottlingFilter(proxyManager));
        registrationBean.addUrlPatterns("/weather/summary");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
