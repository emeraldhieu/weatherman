package com.emeraldhieu.app.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

/**
 * A filter that rate-limits request based on IP address.
 * Learnt from https://github.com/klebertiko/spring-boot-bucket4j-redis-demo/blob/master/src/main/java/com/abddennebi/demo/filter/IpThrottlingFilter.java.
 */
@RequiredArgsConstructor
public class IpThrottlingFilter
    //implements Filter {
    extends GenericFilterBean {

    private final ProxyManager proxyManager;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        BucketConfiguration bucketConfiguration = BucketConfiguration.builder()
            .addLimit(Bandwidth.simple(2, Duration.ofMinutes(1)))
            .build();

        /**
         * Init a bucket whose key is the IP address.
         */
        Bucket bucket = proxyManager.builder()
            // TODO Improve the bucket someway
            .build(httpRequest.getRemoteAddr(), bucketConfiguration);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // the limit is not exceeded
            httpResponse.setHeader("X-Rate-Limit-Remaining", "" + probe.getRemainingTokens());
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            // limit is exceeded
            httpResponse.setContentType("text/plain");
            httpResponse.setStatus(429);
            httpResponse.getWriter().append("Too many requests");
        }
    }
}