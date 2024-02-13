package com.shoppers.ekart.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shoppers.ekart.service.AuthService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ScheduledJobs {

	private AuthService authService;
	
	@Scheduled(cron="0 0 0 ? * *")
	public void removeNonVerifiedUsers() {
		authService.removeNonVerifiedUsers();
	}
	
}
