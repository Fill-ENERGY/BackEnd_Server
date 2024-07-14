package com.example.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SmupoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmupoolApplication.class, args);
	}

}
