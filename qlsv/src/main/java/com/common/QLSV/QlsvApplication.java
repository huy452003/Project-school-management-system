package com.common.QLSV;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {
	org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
	org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class,
	org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
	org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
})
@ComponentScan(basePackages = 
	{"com.common.QLSV", "com.handle_exceptions", "com.logging", "com.common.config"}
)
public class QlsvApplication {

	public static void main(String[] args) {
		SpringApplication.run(QlsvApplication.class, args);
	}

}
