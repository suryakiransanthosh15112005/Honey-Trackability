package com.honeychain.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.base-dir:uploads}")
    private String baseUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(baseUploadDir).toAbsolutePath().normalize().toString()
                .replace("\\", "/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///" + absolutePath + "/");
    }
}
