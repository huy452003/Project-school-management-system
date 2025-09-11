package com.common.QLGV.configurations;

import com.security.config.JwtConfig;
import com.security.services.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({JwtService.class, JwtConfig.class})
public class SecurityImportConfig {}
