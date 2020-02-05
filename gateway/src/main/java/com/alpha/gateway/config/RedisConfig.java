package com.alpha.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

//    private final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;
//
//    @Bean
//    public ReactiveStringRedisTemplate redisTemplate() {
//        return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
//    }

}
