package com.kma.project.expensemanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        // @formatter:off
//        registry
//                .addMapping("/**")
//                .allowedOrigins(CrossOrigin.DEFAULT_ORIGINS)
//                .allowedHeaders(CrossOrigin.DEFAULT_ALLOWED_HEADERS)
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .maxAge(3600L);
//        // @formatter:on
//    }
}