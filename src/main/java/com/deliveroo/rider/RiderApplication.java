package com.deliveroo.rider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RiderApplication {
	public static void main(String[] args) {
		SpringApplication.run(RiderApplication.class, args);
	}

}
