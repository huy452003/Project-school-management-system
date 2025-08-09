package com.common.QLGV;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.common.QLGV", "com.handle_exceptions"})
public class QlgvApplication {
	public static void main(String[] args) {
		SpringApplication.run(QlgvApplication.class, args);
	}

}
