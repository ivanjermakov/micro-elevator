package com.github.ivanjermakov.microelevator.floor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FloorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FloorApplication.class, args);
	}

}
