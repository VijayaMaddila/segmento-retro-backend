package com.retro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RetroApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetroApplication.class, args);
	}

}
