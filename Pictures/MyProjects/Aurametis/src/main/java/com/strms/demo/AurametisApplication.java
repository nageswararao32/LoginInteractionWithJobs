package com.strms.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AurametisApplication {

	public static void main(String[] args) {
		SpringApplication.run(AurametisApplication.class, args);
	}

}
