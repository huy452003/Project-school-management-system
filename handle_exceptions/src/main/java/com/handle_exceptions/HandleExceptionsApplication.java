package com.handle_exceptions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages =
        {"com.logging"}
)
public class HandleExceptionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HandleExceptionsApplication.class, args);
    }

} 