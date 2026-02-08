package com.example.realTimeAuctionSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RealTimeAuctionSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealTimeAuctionSystemApplication.class, args);
	}

}
