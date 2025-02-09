package com.knucse.diy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 엔드포인트에 대해
                .allowedOrigins("http://localhost:3000","https://diy.knucse.site")
                .allowedMethods("GET", "POST", "PUT","PATCH","DELETE") // 허용할 HTTP 메서드
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

