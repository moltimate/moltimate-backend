package org.moltimate.moltimatebackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile("dev")
public class DevConfiguration implements WebMvcConfigurer {

    private static final String ALLOWED_ORIGIN = "http://localhost:8081";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(ALLOWED_ORIGIN);
    }
}
