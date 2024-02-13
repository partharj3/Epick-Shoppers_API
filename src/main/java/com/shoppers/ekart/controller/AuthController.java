package com.shoppers.ekart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppers.ekart.requestdto.OtpModel;
import com.shoppers.ekart.requestdto.UserRequest;
import com.shoppers.ekart.responsedto.UserResponse;
import com.shoppers.ekart.service.AuthService;
import com.shoppers.ekart.util.ResponseStruture;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class AuthController {
	
	private AuthService authService;
	
	@PostMapping("/register")
	public ResponseEntity<ResponseStruture<String>> registerUser(@RequestBody @Valid UserRequest request){
		return authService.registerUser(request);
	}
	
	@PostMapping("/verify-otp")
	public ResponseEntity<ResponseStruture<UserResponse>> verifyOTP(@RequestBody OtpModel otp){
		return authService.verifyOTP(otp);
	}
	
}
