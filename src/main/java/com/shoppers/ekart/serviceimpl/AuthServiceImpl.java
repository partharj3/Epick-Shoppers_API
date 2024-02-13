package com.shoppers.ekart.serviceimpl;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shoppers.ekart.cache.CacheStore;
import com.shoppers.ekart.entity.Customer;
import com.shoppers.ekart.entity.Seller;
import com.shoppers.ekart.entity.User;
import com.shoppers.ekart.enums.UserRole;
import com.shoppers.ekart.exception.IllegalRequestException;
import com.shoppers.ekart.exception.UserAleadyExistsByEmailException;
import com.shoppers.ekart.repository.CustomerRepository;
import com.shoppers.ekart.repository.SellerRepository;
import com.shoppers.ekart.repository.UserRepository;
import com.shoppers.ekart.requestdto.OtpModel;
import com.shoppers.ekart.requestdto.UserRequest;
import com.shoppers.ekart.responsedto.UserResponse;
import com.shoppers.ekart.service.AuthService;
import com.shoppers.ekart.util.MessageStructure;
import com.shoppers.ekart.util.ResponseStruture;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{

	private UserRepository userRepo;
	private SellerRepository sellerRepo;
	private CustomerRepository customerRepo;
	
	private PasswordEncoder encoder;
	
	private ResponseStruture<UserResponse> structure;
	
	private CacheStore<Integer> otpCacheStore;
	
	private CacheStore<User> userCacheStore;
	
	private JavaMailSender javaMailSender;
	
	@Override
	public ResponseEntity<ResponseStruture<String>> registerUser(UserRequest request) {
		  
		if(userRepo.existsByEmail(request.getEmail())) throw new UserAleadyExistsByEmailException("Registration failed !!");
		
		int otp = createOTP();
		User user = mapToUser(request); 
		
		userCacheStore.add(user.getEmail(), user);
		otpCacheStore.add(user.getEmail(), otp);
		
		try {
			sendOtpToMail(user, otp);
		} catch (MessagingException e) {
			log.error("This Email address does not exist");
		}
		
		ResponseStruture<String> structure =new ResponseStruture<>();
		
	    return new ResponseEntity<ResponseStruture<String>>(
			       structure.setStatusCode(HttpStatus.ACCEPTED.value())
				            .setMessage("OTP sent to your Email: "+user.getEmail())
					        .setData(user.getUsername()+", Kindly verify your email"), HttpStatus.ACCEPTED);
		  
	}

	@Override
	public void removeNonVerifiedUsers() {
		List<User> toBeDeleted = userRepo.findByIsEmailVerifiedFalse();
		if(!toBeDeleted.isEmpty()) {
			toBeDeleted.forEach(user -> userRepo.delete(user));
			System.out.println("Cleared Email Non-Verified Users");
		}		
	}

//	@Transactional
//    public void removeNonVerifiedUsers() {
//        userRepo.deleteByIsEmailVerifiedFalse();
//    }

	@Override
	public ResponseEntity<ResponseStruture<UserResponse>> verifyOTP(OtpModel otpModel) {
		User user = userCacheStore.get(otpModel.getEmail());
		int otp = otpCacheStore.get(otpModel.getEmail());
		
		if(user==null) throw new IllegalRequestException("User Session Expired !!");
		if(otp!=0) {
			if(otp==otpModel.getOtp()) {
				user.setEmailVerified(true);
				userRepo.save(user);
				
				try {
					sendRegistrationSuccessMail(user);
				} catch (MessagingException e) {
					log.error("This Email address does not exist");
				}
				
				return new ResponseEntity<ResponseStruture<UserResponse>>(
					       structure.setStatusCode(HttpStatus.CREATED.value())
				            .setMessage("Registration Successfull")
					        .setData(mapToUserResponse(user)), HttpStatus.CREATED);
			}
			throw new IllegalRequestException("OTP mis-matched !!");
		}
		throw new IllegalRequestException("OTP has been expired");
	}
	
	/*
	 * If the method is returning something, then it would be SYNCHRONOUS.  
	 * If the method has to send mail to multiple clients, then it should be ASYNCHRONOUS for the better performance
	 * without any delay. 
	 * 
	 * Here the daemon thread works to make it async.
	 * 
	 * @param message
	 */
	
	@Async
	public void sendMail(MessageStructure message) throws MessagingException {
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // is the mail contains multipart file
		
		helper.setTo(message.getTo());
		helper.setSubject(message.getSubject());
		helper.setSentDate(message.getSentDate());
		helper.setText(message.getText(), true); // if the text contains html file: true
		
		javaMailSender.send(mimeMessage);
		
	}

	private void sendRegistrationSuccessMail(User user) throws MessagingException {
		sendMail(MessageStructure.builder()
				.to(user.getEmail())
				.subject("Registration Successfull - epick.Shopper")
				.sentDate(new Date())
				.text(
						"Welcome buddy <span style='font-size: 20px;'>"+user.getUsername()
						+"</span><br>- Your registration is successfully completed."
						+"Best Regards"
						+"<h2>ePick.Shopper</h2>"
					 )
				.build());
	}
	
	private void sendOtpToMail(User user, int otp) throws MessagingException  {
		sendMail(MessageStructure.builder()
				.to(user.getEmail())
				.subject("Email Verification Mail - epick.Shopper")
				.sentDate(new Date())
				.text(
						"hey, "+user.getUsername()
						+"<br>Welcome to our epic clubs, so please get started by completing your verification using the OTP: "
						+"<h1>"+otp+"</h1>"
						+"<h5>Note: the otp expires in 2 minutes</h5>"+"<br><br>"
						+"with best regards"
						+"epicK.Shopper"
					 )
				.build()
				);
	}
	
	private int createOTP() {
	    return new Random().nextInt(100000,999999);
	}

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
	
	
}