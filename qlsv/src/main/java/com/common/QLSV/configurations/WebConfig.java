package com.common.QLSV.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {registry.addInterceptor(jwtInterceptor)
                .excludePathPatterns("**/public/**", "/error/**")  // Exclude public and error paths
                .addPathPatterns("/**");  // Apply to all paths including /students

    }
} 