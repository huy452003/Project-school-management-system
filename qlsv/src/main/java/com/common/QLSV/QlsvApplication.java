package com.common.QLSV;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = 
	{"com.common.QLSV", "com.handle_exceptions", "com.logging"}
)
public class QlsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(QlsvApplication.class, args);
	}

}
