package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceLocation;

        if (uploadDir.startsWith("/")) {
            resourceLocation = "file:" + uploadDir + "/";
        } else {
            resourceLocation = "file:./" + uploadDir + "/";
        }

        registry.addResourceHandler("/images/**")
                .addResourceLocations(resourceLocation);
    }
}