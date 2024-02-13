package com.shoppers.ekart.serviceimpl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shoppers.ekart.cache.CacheStore;
import com.shoppers.ekart.entity.AccessToken;
import com.shoppers.ekart.entity.Customer;
import com.shoppers.ekart.entity.RefreshToken;
import com.shoppers.ekart.entity.Seller;
import com.shoppers.ekart.entity.User;
import com.shoppers.ekart.enums.UserRole;
import com.shoppers.ekart.exception.IllegalRequestException;
import com.shoppers.ekart.exception.OtpNotVerifiedException;
import com.shoppers.ekart.exception.UserAleadyExistsByEmailException;
import com.shoppers.ekart.exception.UserAlreadyLoggedInException;
import com.shoppers.ekart.exception.UserNotLoggedInException;
import com.shoppers.ekart.repository.AccessTokenRepository;
import com.shoppers.ekart.repository.CustomerRepository;
import com.shoppers.ekart.repository.RefreshTokenRepository;
import com.shoppers.ekart.repository.SellerRepository;
import com.shoppers.ekart.repository.UserRepository;
import com.shoppers.ekart.requestdto.AuthRequest;
import com.shoppers.ekart.requestdto.OtpModel;
import com.shoppers.ekart.requestdto.UserRequest;
import com.shoppers.ekart.responsedto.AuthResponse;
import com.shoppers.ekart.responsedto.UserResponse;
import com.shoppers.ekart.security.JwtService;
import com.shoppers.ekart.service.AuthService;
import com.shoppers.ekart.util.CookieManager;
import com.shoppers.ekart.util.MessageStructure;
import com.shoppers.ekart.util.ResponseStructure;
import com.shoppers.ekart.util.SimpleResponseStructure;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService{

	private UserRepository userRepo;
	private SellerRepository sellerRepo;
	private CustomerRepository customerRepo;
	private PasswordEncoder encoder;
	private ResponseStructure<UserResponse> structure;
	private ResponseStructure<AuthResponse> authStructure;
	private CacheStore<Integer> otpCacheStore;
	private CacheStore<User> userCacheStore;
	private JavaMailSender javaMailSender;
	private AuthenticationManager authManager;
	private CookieManager cookieManager;
	private JwtService jwtService;
	private AccessTokenRepository accessTokenRepo;
	private RefreshTokenRepository refreshTokenRepo;
	
	@Value("${myapp.access.expiry}")
	private int accessExpiryInSeconds;
	
	@Value("${myapp.refresh.expiry}")
	private int refreshExpiryInSeconds;
	
	public AuthServiceImpl(UserRepository userRepo, 
				           SellerRepository sellerRepo, 
				           CustomerRepository customerRepo,
						   PasswordEncoder encoder, 
						   ResponseStructure<UserResponse> structure,
						   ResponseStructure<AuthResponse> authStructure,
						   CacheStore<Integer> otpCacheStore,
						   CacheStore<User> userCacheStore, 
						   JavaMailSender javaMailSender, 
						   AuthenticationManager authManager,
						   CookieManager cookieManager,
						   JwtService jwtService,
						   AccessTokenRepository accessTokenRepo,
						   RefreshTokenRepository refreshTokenRepo) {
	super();
	this.userRepo = userRepo;
	this.sellerRepo = sellerRepo;
	this.customerRepo = customerRepo;
	this.encoder = encoder;
	this.structure = structure;
	this.authStructure = authStructure;
	this.otpCacheStore = otpCacheStore;
	this.userCacheStore = userCacheStore;
	this.javaMailSender = javaMailSender;
	this.authManager = authManager;
	this.cookieManager = cookieManager;
	this.jwtService = jwtService;
	this.accessTokenRepo = accessTokenRepo;
	this.refreshTokenRepo = refreshTokenRepo;
	}
	
	@Override
	public ResponseEntity<ResponseStructure<String>> registerUser(UserRequest request) {
		  
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
		
		ResponseStructure<String> structure =new ResponseStructure<>();
		
	    return new ResponseEntity<ResponseStructure<String>>(
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
	public ResponseEntity<ResponseStructure<UserResponse>> verifyOTP(OtpModel otpModel) {
		User user = userCacheStore.get(otpModel.getEmail());
		Integer otp = otpCacheStore.get(otpModel.getEmail());
		
		if(user==null) throw new OtpNotVerifiedException("User Session Expired !!");
		if(otp==null) throw new OtpNotVerifiedException("OTP has been expired");
		if(otp!=otpModel.getOtp()) throw new OtpNotVerifiedException("OTP mis-matched !!");
		
		user.setEmailVerified(true);
		userRepo.save(user);
		
		try {
			sendRegistrationSuccessMail(user);
		} catch (MessagingException e) {
			log.error("This Email address does not exist");
		}
		
		return new ResponseEntity<ResponseStructure<UserResponse>>(
			       structure.setStatusCode(HttpStatus.CREATED.value())
		            .setMessage("Registration Successful")
			        .setData(mapToUserResponse(user)), HttpStatus.CREATED);
		
	}
	
	@Override
	public ResponseEntity<ResponseStructure<AuthResponse>> login(String at, String rt, AuthRequest request,HttpServletResponse response) {
		if(at==null && rt==null) {
			String username = request.getEmail().split("@")[0];
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, request.getPassword());
			
			Authentication authentication = authManager.authenticate(token);
			if(!authentication.isAuthenticated()) 
				throw new UsernameNotFoundException("Failed to Authenticate the user");
			else 
				//generating the cookies and auth-response and returning to the client.
				return userRepo.findByUsername(username).map(user ->{
					grantAccess(response, user);
					return ResponseEntity
							.ok(authStructure.setStatusCode(HttpStatus.OK.value())
							.setMessage("Login Successful")
							.setData(AuthResponse.builder()
									.userId(user.getUserId())
									.username(username)
									.role(user.getUserRole().name())
									.isAuthenticated(true)
									.accessExpiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
									.refreshExpiration(LocalDateTime.now().plusSeconds(refreshExpiryInSeconds))
									.build()));
				}).get();
		}
		throw new UserAlreadyLoggedInException("Already Logged in");
	}
	
	@Override
	public ResponseEntity<String> traditionalLogout(HttpServletRequest request, HttpServletResponse response) {
		
		String at = "",rt="";
		
		Cookie[] cookies = request.getCookies();
		
		for(Cookie cookie: cookies) {
			if(cookie.getName().equals("at")) at=cookie.getValue();
			if(cookie.getName().equals("rt")) rt=cookie.getValue();
		}
		
		accessTokenRepo.findByToken(at).ifPresent( accessToken ->{
			accessToken.setBlocked(true);
			accessTokenRepo.save(accessToken);
		});
		response.addCookie(cookieManager.invalidate(new Cookie("at","")));
		
		refreshTokenRepo.findByToken(rt).ifPresent( refreshToken ->{
			refreshToken.setBlocked(true);
			refreshTokenRepo.save(refreshToken);
		});
		response.addCookie(cookieManager.invalidate(new Cookie("rt","")));
		
		return new ResponseEntity<String>("Logout Successfully !",HttpStatus.OK);
	}

	@Override
	public ResponseEntity<SimpleResponseStructure> logout(String accessToken, String refreshToken,HttpServletResponse response) {
		
		if(accessToken == null && refreshToken == null)
			throw new UserNotLoggedInException("Please do log in");
		
		accessTokenRepo.findByToken(accessToken).ifPresent(at ->{
			at.setBlocked(true);
			accessTokenRepo.save(at);
		});
		response.addCookie(cookieManager.invalidate(new Cookie("at","")));
		
		refreshTokenRepo.findByToken(refreshToken).ifPresent(rt ->{
			rt.setBlocked(true);
			refreshTokenRepo.save(rt);
		});
		response.addCookie(cookieManager.invalidate(new Cookie("rt","")));
		
		SimpleResponseStructure structure = new SimpleResponseStructure();
		structure.setStatusCode(HttpStatus.OK.value());
		structure.setMessage("Logout Successfully");
		
		return new ResponseEntity<SimpleResponseStructure>(structure, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<SimpleResponseStructure> revokeOtherDevicesAccess(String accessToken, String refreshToken, HttpServletResponse response) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if(username!= null) {
			userRepo.findByUsername(username).ifPresent(user ->{
				blockAccessTokens(accessTokenRepo.findByUserAndIsBlockedAndTokenNot(user, false, accessToken));
				blockRefreshTokens(refreshTokenRepo.findByUserAndIsBlockedAndTokenNot(user, false, refreshToken));
			});
			response.addCookie(cookieManager.invalidate(new Cookie("at", "")));
			response.addCookie(cookieManager.invalidate(new Cookie("rt", "")));
			
			SimpleResponseStructure simpleStructure = new SimpleResponseStructure();
			simpleStructure.setStatusCode(HttpStatus.OK.value());
			simpleStructure.setMessage("Access revoked from other devices successfully");
		}
		throw new IllegalRequestException("You are not logged in any other devices");
	}

	@Override
	public void cleanupExpiredAccessTokens() {
		accessTokenRepo.deleteAll(accessTokenRepo.findAllByExpirationBefore(LocalDateTime.now()));
	}

	@Override
	public void cleanupExpiredRefreshTokens() {
		refreshTokenRepo.deleteAll(refreshTokenRepo.findAllByExpirationBefore(LocalDateTime.now()));
	}
	
	@Override
	public ResponseEntity<SimpleResponseStructure> revokeAllDevicesAccess(String accessToken, String refreshToken,
			HttpServletResponse response) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if(username == null) throw new UserNotLoggedInException("Please Log in");
		
		userRepo.findByUsername(username).ifPresent(user ->{
			blockAccessTokens(accessTokenRepo.findByTokenAndIsBlocked(user, false));
			blockRefreshTokens(refreshTokenRepo.findByTokenAndIsBlocked(user, false));
		});
		
		response.addCookie(cookieManager.invalidate(new Cookie("at", "")));
		response.addCookie(cookieManager.invalidate(new Cookie("rt", "")));
		
		SimpleResponseStructure simpleStructure = new SimpleResponseStructure();
		simpleStructure.setStatusCode(HttpStatus.OK.value());
		simpleStructure.setMessage("Successfully logged out from all devices");
		
		return new ResponseEntity<SimpleResponseStructure>(simpleStructure, HttpStatus.OK);
	}
	
	/******************** PRIVATE OPERATIONS ******************************/
	
	private void blockRefreshTokens(List<RefreshToken> refreshTokens) {
		refreshTokens.forEach(refToken ->{
			refToken.setBlocked(true);
			refreshTokenRepo.save(refToken);
		});
	}

	private void blockAccessTokens(List<AccessToken> accessTokens) {
		accessTokens.forEach(aToken ->{
			aToken.setBlocked(true);
			accessTokenRepo.save(aToken);
		});
	}

	private void grantAccess(HttpServletResponse response, User user) {
		
		/**
		 * Generating refresh token &
		 * 
		 * Generating access token for the initial request (login)
		 * and for each refresh by the refresh token.
		 */
		
		String accessToken = jwtService.generateAccessToken(user.getUsername());
		String refreshToken = jwtService.generateRefreshToken(user.getUsername());
		
		/** Adding access and refresh tokens cookies to the response  */
		 
		response.addCookie(cookieManager.configure(new Cookie("at", accessToken), accessExpiryInSeconds));
		response.addCookie(cookieManager.configure(new Cookie("rt", refreshToken), refreshExpiryInSeconds));
		
		/**-- saving the access & refresh cookie into the database --*/
		
		accessTokenRepo.save(AccessToken.builder()
							.token(accessToken)
							.isBlocked(false)
							.user(user)
							.expiration(LocalDateTime.now().plusSeconds(accessExpiryInSeconds))
							.build());
		
		refreshTokenRepo.save(RefreshToken.builder()
							.token(refreshToken)
							.isBlocked(false)
							.user(user)
							.expiration(LocalDateTime.now().plusSeconds(refreshExpiryInSeconds))
							.build());
		
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
	private void sendMail(MessageStructure message) throws MessagingException {
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
				.subject("Registration Successful - epick.Shopper")
				.sentDate(new Date())
				.text(
						"Welcome buddy <span style='font-size: 13px; font-weight: bold;'>"+user.getUsername()+"</span>,"
						+"<br>- Your registration is successfully completed.<br><br>"
						+"Best Regards,<br>"
						+"<span style='font-size: 15px; font-weight: bold;'>ePick.Shopper</span>"
					 )
				.build());
	}
	
	private void sendOtpToMail(User user, int otp) throws MessagingException  {
		sendMail(MessageStructure.builder()
				.to(user.getEmail())
				.subject("Email Verification Mail - epick.Shopper")
				.sentDate(new Date())
				.text(
						"hey, <span style='font-size: 13px; font-weight: bold;'>"+user.getUsername()+"</span>"
						+"<br>Welcome to our epic's club, please get started by completing your verification using the OTP: "
						+"<h1>"+otp+"</h1>"
						+"<h5>Note: the otp expires in 2 minutes</h5>"
						+"with best regards,<br>"
						+"<span style='font-size: 15px; font-weight: bold;'>ePick.Shopper</span>"
					 )
				.build());
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
