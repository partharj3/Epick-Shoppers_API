package com.shoppers.ekart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class EpickShopppersApplication {
	public static void main(String[] args) {
		SpringApplication.run(EpickShopppersApplication.class, args);
		System.out.println("Go Shop...!!!");
	}
}