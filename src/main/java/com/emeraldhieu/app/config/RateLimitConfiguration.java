package com.emeraldhieu.app.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.redisson.cas.RedissonBasedProxyManager;
import lombok.RequiredArgsConstructor;
import org.redisson.command.CommandExecutor;
import org.redisson.command.CommandSyncService;
import org.redisson.config.Config;
import org.redisson.config.ConfigSupport;
import org.redisson.connection.ConnectionManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(value = "application.redis.enabled", havingValue = "true")
@RequiredArgsConstructor
public class RateLimitConfiguration {

    private final RateLimitProperties rateLimitProperties;

    @Bean
    public BucketConfiguration bucketConfiguration() {
        return BucketConfiguration.builder()
            .addLimit(Bandwidth.simple(rateLimitProperties.getRequestCount(),
                Duration.ofSeconds(rateLimitProperties.getDurationInSeconds())))
            .build();
    }

    @Bean
    public ProxyManager<String> proxyManager(Config config) {
        ConnectionManager connectionManager = ConfigSupport.createConnectionManager(config);
        CommandExecutor commandExecutor = new CommandSyncService(connectionManager, null);
        return RedissonBasedProxyManager.builderFor(commandExecutor)
            .withExpirationStrategy(ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofSeconds(10)))
            .build();
    }
}
