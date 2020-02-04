package com.alpha.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.io.JacksonDeserializer;
import io.jsonwebtoken.io.JacksonSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public JacksonSerializer jacksonSerializer() {
        return new JacksonSerializer(objectMapper);
    }


}
