package com.ticket.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    

    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        
        registry.addResourceHandler("/banner/**")
                .addResourceLocations("file:///C:/Ticket/banner/");
        
        registry.addResourceHandler("/itemImg/**")
              .addResourceLocations("file:///C:/Ticket/itemImg/");
        
    }
}
