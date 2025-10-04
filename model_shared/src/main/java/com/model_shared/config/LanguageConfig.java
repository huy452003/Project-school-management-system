package com.model_shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LanguageConfig implements WebMvcConfigurer {

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource message = new ReloadableResourceBundleMessageSource();
        
        message.setBasename("classpath:messages");
        message.setDefaultEncoding("UTF-8");
        message.setCacheSeconds(3600); // Cache for 1 hour
        message.setUseCodeAsDefaultMessage(true); // Use key as fallback if message not found
        
        return message;
    }
}
