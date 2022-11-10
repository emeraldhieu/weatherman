package com.emeraldhieu.app.config;

import com.emeraldhieu.app.forecast.exception.TooManyRequestException;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A filter that rate-limits request based on IP address.
 * Learnt from https://github.com/klebertiko/spring-boot-bucket4j-redis-demo/blob/master/src/main/java/com/abddennebi/demo/filter/IpThrottlingFilter.java.
 */
@RequiredArgsConstructor
public class IpThrottlingFilter extends GenericFilterBean {

    private static final String RATE_LIMIT_NAMESPACE = "rateLimit:";
    private final ProxyManager proxyManager;
    private final BucketConfiguration bucketConfiguration;
    private final HandlerExceptionResolver exceptionResolver;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        Bucket bucket = getBucket(httpRequest.getRemoteAddr());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // The limit is not exceeded.
            httpResponse.setHeader("X-Rate-Limit-Remaining", "" + probe.getRemainingTokens());
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            // The limit is exceeded.
            exceptionResolver.resolveException(httpRequest, httpResponse, null,
                new TooManyRequestException("Rate limit has been reached"));
        }
    }

    private Bucket getBucket(String ipAddress) {
        // Replace colon with underscore for Redis not to categorize by colon
        String ipAddressToStore = ipAddress.replace(":", "_");
        String rateLimitCacheKey = RATE_LIMIT_NAMESPACE + ipAddressToStore;

        // Init a bucket whose key is the IP address.
        Bucket bucket = proxyManager.builder()
            .build(rateLimitCacheKey, bucketConfiguration);
        return bucket;
    }
}