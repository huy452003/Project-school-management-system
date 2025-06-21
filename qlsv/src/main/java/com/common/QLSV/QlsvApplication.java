package com.common.QLSV;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class QlsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(QlsvApplication.class, args);
	}

}
