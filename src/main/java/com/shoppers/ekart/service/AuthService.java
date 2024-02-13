package com.shoppers.ekart.service;

import org.springframework.http.ResponseEntity;

import com.shoppers.ekart.requestdto.AuthRequest;
import com.shoppers.ekart.requestdto.OtpModel;
import com.shoppers.ekart.requestdto.UserRequest;
import com.shoppers.ekart.responsedto.AuthResponse;
import com.shoppers.ekart.responsedto.UserResponse;
import com.shoppers.ekart.util.ResponseStruture;

import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
	ResponseEntity<ResponseStruture<String>> registerUser(UserRequest request);

	void removeNonVerifiedUsers();

	ResponseEntity<ResponseStruture<UserResponse>> verifyOTP(OtpModel otp);
	
	public ResponseEntity<ResponseStruture<AuthResponse>> login(String at, String rt, AuthRequest request,HttpServletResponse response);
}
