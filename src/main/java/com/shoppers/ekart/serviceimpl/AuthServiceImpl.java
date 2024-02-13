package com.shoppers.ekart.serviceimpl;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shoppers.ekart.entity.Customer;
import com.shoppers.ekart.entity.Seller;
import com.shoppers.ekart.entity.User;
import com.shoppers.ekart.enums.UserRole;
import com.shoppers.ekart.exception.IllegalRequestException;
import com.shoppers.ekart.exception.UserAleadyExistsByEmailException;
import com.shoppers.ekart.repository.CustomerRepository;
import com.shoppers.ekart.repository.SellerRepository;
import com.shoppers.ekart.repository.UserRepository;
import com.shoppers.ekart.requestdto.UserRequest;
import com.shoppers.ekart.responsedto.UserResponse;
import com.shoppers.ekart.service.AuthService;
import com.shoppers.ekart.util.ResponseStruture;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{

	private UserRepository userRepo;
	private SellerRepository sellerRepo;
	private CustomerRepository customerRepo;
	
	private PasswordEncoder encoder;
	
	private ResponseStruture<UserResponse> structure;
	
	private <T extends User>T mapToUser(UserRequest request) {
		
		User user = null;
		switch(request.getUserRole()) {
			case "CUSTOMER" -> {user = new Customer();}
			case "SELLER" -> {user = new Seller();}
		}
		
		user.setUsername(request.getEmail().split("@")[0]);
		user.setEmail(request.getEmail());
		user.setPassword(encoder.encode(request.getPassword()));
		user.setUserRole(UserRole.valueOf(request.getUserRole()));
		return (T)user;
	}
	
	private User saveUser(UserRequest request) {
		User user = mapToUser(request);
		switch(user.getUserRole()) {
			case CUSTOMER -> { user = customerRepo.save((Customer)user);}
		  	case SELLER -> {user = sellerRepo.save((Seller)user);}
		  	default -> {throw new IllegalRequestException("Invalid User Role");}
		}
		return user;
	}
	
	private UserResponse mapToUserResponse(User user) {
		return UserResponse.builder()
				.userId(user.getUserId())
				.username(user.getUsername())
				.email(user.getEmail())
				.userRole(user.getUserRole())
				.build();
	}
	
	@Override
	public ResponseEntity<ResponseStruture<UserResponse>> registerUser(UserRequest request) {
		  User user = userRepo.findByEmail(request.getEmail())
				  .map(u ->{
						  if(u.isEmailVerified()) throw new UserAleadyExistsByEmailException("Registeration Failed");
						  else {
							  // send otp to email
						  }
						  return u;
					  	})
				  .orElseGet(() -> saveUser(request));
		  
		  return new ResponseEntity<ResponseStruture<UserResponse>>(
				  structure.setStatusCode(HttpStatus.ACCEPTED.value())
				           .setMessage("Kindly verify your email by OTP sent to your email")
				           .setData(mapToUserResponse(user)), HttpStatus.ACCEPTED);
		  
	}

	@Override
	public void removeNonVerifiedUsers() {
		List<User> toBeDeleted = userRepo.findByIsEmailVerifiedFalse();
		if(!toBeDeleted.isEmpty()) {
			toBeDeleted.forEach(user -> userRepo.delete(user));
			System.out.println("Cleared Email Non-Verified Users");
		}
	}

	
	
}