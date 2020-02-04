package com.alpha.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.JacksonDeserializer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public JacksonDeserializer<Map<String, ?>> jacksonDeserializer() {
        return new JacksonDeserializer<>(objectMapper);
    }
}
