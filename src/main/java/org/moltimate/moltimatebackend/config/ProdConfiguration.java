package org.moltimate.moltimatebackend.config;

import com.sun.xml.bind.v2.TODO;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableRetry
@Profile("prod")
public class ProdConfiguration implements WebMvcConfigurer {

    private static final String ALLOWED_ORIGIN = "http://moltimate.us-east-1.elasticbeanstalk.com";

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(ALLOWED_ORIGIN);
    }
}