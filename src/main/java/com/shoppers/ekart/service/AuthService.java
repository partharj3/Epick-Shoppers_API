package com.shoppers.ekart.service;

import org.springframework.http.ResponseEntity;

import com.shoppers.ekart.requestdto.OtpModel;
import com.shoppers.ekart.requestdto.UserRequest;
import com.shoppers.ekart.responsedto.UserResponse;
import com.shoppers.ekart.util.ResponseStruture;

public interface AuthService {
	ResponseEntity<ResponseStruture<String>> registerUser(UserRequest request);

	void removeNonVerifiedUsers();

	ResponseEntity<ResponseStruture<UserResponse>> verifyOTP(OtpModel otp);
}
