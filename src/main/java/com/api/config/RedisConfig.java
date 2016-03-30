package com.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class RedisConfig extends RedisAutoConfiguration {

    @Value("${redis.host:192.168.99.100}")
    private String host;

    @Value("${redis.port:32775}")
    private int port;

    @Value("${redis.timeout:60}")
    private int timeout;

    @Override
    public RedisProperties redisProperties() {
        RedisProperties properties = new RedisProperties();
        properties.setHost(host);
        properties.setPort(port);
        properties.setTimeout(timeout);
        return  properties;
    }

}
