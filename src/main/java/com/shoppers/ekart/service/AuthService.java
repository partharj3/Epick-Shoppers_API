package com.shoppers.ekart.service;

import org.springframework.http.ResponseEntity;

import com.shoppers.ekart.requestdto.AuthRequest;
import com.shoppers.ekart.requestdto.OtpModel;
import com.shoppers.ekart.requestdto.UserRequest;
import com.shoppers.ekart.responsedto.AuthResponse;
import com.shoppers.ekart.responsedto.UserResponse;
import com.shoppers.ekart.util.ResponseStructure;
import com.shoppers.ekart.util.SimpleResponseStructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
	ResponseEntity<ResponseStructure<String>> registerUser(UserRequest request);

	void removeNonVerifiedUsers();

	ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OtpModel otp);
	
	public ResponseEntity<ResponseStructure<AuthResponse>> login(String at, String rt, AuthRequest request,HttpServletResponse response);

	ResponseEntity<String> traditionalLogout(HttpServletRequest request,
			HttpServletResponse response);

	ResponseEntity<SimpleResponseStructure> logout(String accessToken, String refreshToken,
			HttpServletResponse response);

	ResponseEntity<SimpleResponseStructure> revokeOtherDevicesAccess(String accessToken, String refreshToken,
			HttpServletResponse response);
	
	public void cleanupExpiredAccessTokens();
	
	public void cleanupExpiredRefreshTokens();

	ResponseEntity<SimpleResponseStructure> revokeAllDevicesAccess(String accessToken, String refreshToken,
			HttpServletResponse response);
}
