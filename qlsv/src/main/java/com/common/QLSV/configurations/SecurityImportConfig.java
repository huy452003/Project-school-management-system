package com.common.QLSV.configurations;

import com.security.services.JwtService;
import com.security.config.JwtConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({JwtService.class, JwtConfig.class})
public class SecurityImportConfig {} 