package com.common.QLSV.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Kiểm tra DATABASE_URL từ environment variable (Railway format)
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            // Railway cung cấp DATABASE_URL dạng: mysql://user:password@host:port/dbname
            // Hoặc: postgresql://user:password@host:port/dbname
            try {
                // Nếu đã là JDBC format, dùng trực tiếp
                if (databaseUrl.startsWith("jdbc:")) {
                    return DataSourceBuilder.create()
                            .url(databaseUrl)
                            .username(username)
                            .password(password)
                            .driverClassName(driverClassName)
                            .build();
                }
                
                // Parse Railway format: mysql://user:password@host:port/dbname
                URI uri = new URI(databaseUrl);
                String scheme = uri.getScheme(); // mysql hoặc postgresql
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? getDefaultPort(scheme) : uri.getPort();
                String path = uri.getPath().startsWith("/") ? uri.getPath().substring(1) : uri.getPath();
                
                // Parse user info
                String userInfo = uri.getUserInfo();
                String dbUsername = username;
                String dbPassword = password;
                
                if (userInfo != null && !userInfo.isEmpty()) {
                    if (userInfo.contains(":")) {
                        String[] parts = userInfo.split(":", 2);
                        dbUsername = parts[0];
                        dbPassword = parts.length > 1 ? parts[1] : "";
                    } else {
                        dbUsername = userInfo;
                    }
                }
                
                // Convert sang JDBC format
                String jdbcUrl;
                if ("postgresql".equals(scheme) || "postgres".equals(scheme)) {
                    jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, path);
                    driverClassName = "org.postgresql.Driver";
                } else if ("mysql".equals(scheme)) {
                    jdbcUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, path);
                    driverClassName = "com.mysql.cj.jdbc.Driver";
                } else {
                    // Fallback
                    jdbcUrl = String.format("jdbc:%s://%s:%d/%s", scheme, host, port, path);
                }
                
                return DataSourceBuilder.create()
                        .url(jdbcUrl)
                        .username(dbUsername)
                        .password(dbPassword)
                        .driverClassName(driverClassName)
                        .build();
            } catch (Exception e) {
                // Nếu parse lỗi, fallback về default configuration
                System.err.println("Error parsing DATABASE_URL: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Fallback: dùng configuration từ application.properties
        return DataSourceBuilder.create()
                .url(datasourceUrl)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }
    
    private int getDefaultPort(String scheme) {
        switch (scheme) {
            case "postgresql":
            case "postgres":
                return 5432;
            case "mysql":
                return 3306;
            default:
                return 3306;
        }
    }
}

