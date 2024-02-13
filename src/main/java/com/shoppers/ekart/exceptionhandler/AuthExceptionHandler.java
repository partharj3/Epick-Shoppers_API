package com.shoppers.ekart.exceptionhandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.shoppers.ekart.exception.IllegalRequestException;
import com.shoppers.ekart.exception.OtpNotVerifiedException;
import com.shoppers.ekart.exception.UserAleadyExistsByEmailException;
import com.shoppers.ekart.exception.UserAlreadyLoggedInException;
import com.shoppers.ekart.exception.UserNotLoggedInException;

@RestControllerAdvice
public class AuthExceptionHandler extends ResponseEntityExceptionHandler{

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		List<ObjectError> allErrors = ex.getAllErrors(); // To get the list of errors
		
		Map<String, String> errors = new HashMap<>();    //  To display in the form of Key Value Pair
		
		allErrors.forEach(error -> {					 // To iterate each error 
			FieldError fieldError = (FieldError)error;   // Field Error is nothing but an error occurred to the Field.
			errors.put(fieldError.getField(),fieldError.getDefaultMessage());
		});	
		return structure(HttpStatus.BAD_REQUEST,"Failed to save the Data", errors);
	}
	
	public ResponseEntity<Object> structure(HttpStatus status, String message, Object rootcause){
		return new ResponseEntity<Object>
		       (Map.of(
						"rootcause",rootcause
						,"status",status.value()
						,"message",message
				),status);
		// ResponseEntity<Object>(ResponseStructure OBJECT in the form of MAP, HttpStatus);
	}
	
	@ExceptionHandler(UserAleadyExistsByEmailException.class)
	private ResponseEntity<Object> handleDataAlreadyExistsException(UserAleadyExistsByEmailException exp){
		return structure(HttpStatus.FOUND, exp.getMessage(), "User already exists with this Email");
	}
	
	@ExceptionHandler(IllegalRequestException.class)
	private ResponseEntity<Object> handleIllegalRequestException(IllegalRequestException exp){
		return structure(HttpStatus.UNAUTHORIZED, exp.getMessage(), "Failed to proceed further");
	}
	
	@ExceptionHandler(OtpNotVerifiedException.class)
	private ResponseEntity<Object> handleOtpVerificationException(OtpNotVerifiedException exp){
		return structure(HttpStatus.EXPECTATION_FAILED, exp.getMessage(), "Failed to Verify the OTP");
	}
	
	
	@ExceptionHandler(UserAlreadyLoggedInException.class)
	private ResponseEntity<Object> handleUserAlreadyLoggedInException(UserAlreadyLoggedInException exp){
		return structure(HttpStatus.OK, exp.getMessage(), "Already Logged In");
	}
	
	@ExceptionHandler(UserNotLoggedInException.class)
	private ResponseEntity<Object> handleUserNotLoggedInException(UserNotLoggedInException exp){
		return structure(HttpStatus.BAD_REQUEST, exp.getMessage(), "User not Logged in");
	}
	
}
