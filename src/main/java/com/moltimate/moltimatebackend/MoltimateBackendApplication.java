package com.moltimate.moltimatebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MoltimateBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoltimateBackendApplication.class, args);
	}

	@Bean
	public TestController testController() {
		return new TestController();
	}
}
