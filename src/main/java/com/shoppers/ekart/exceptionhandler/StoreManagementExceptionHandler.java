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

import com.shoppers.ekart.exception.AddressNotExistsWithThisIdException;
import com.shoppers.ekart.exception.NoStoreDataExistsException;
import com.shoppers.ekart.exception.StoreAddressNotFoundException;
import com.shoppers.ekart.exception.StoreAlreadyExistsWithNameAndAddressException;
import com.shoppers.ekart.exception.StoreAlreadyExistsWithSellerException;
import com.shoppers.ekart.exception.StoreAlreadyHasAddressException;
import com.shoppers.ekart.exception.StoreNotFoundByIdException;

@RestControllerAdvice
public class StoreManagementExceptionHandler extends ResponseEntityExceptionHandler{

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
	
	@ExceptionHandler(StoreAlreadyExistsWithNameAndAddressException.class)
	private ResponseEntity<Object> handleStoreNameAlreadyExistsException(StoreAlreadyExistsWithNameAndAddressException exp){
		return structure(HttpStatus.FOUND, exp.getMessage(), "Store Data already inserted");
	}
	
	@ExceptionHandler(StoreNotFoundByIdException.class)
	private ResponseEntity<Object> handleSStoreNotFoundByIdException(StoreNotFoundByIdException exp){
		return structure(HttpStatus.NOT_FOUND, exp.getMessage(), "Store Data not exists with this ID");
	}
	
	@ExceptionHandler(NoStoreDataExistsException.class)
	private ResponseEntity<Object> handleNoStoreDataExistsException(NoStoreDataExistsException exp){
		return structure(HttpStatus.NOT_FOUND, exp.getMessage(), "Store List is Empty");
	}
	
	@ExceptionHandler(StoreAlreadyExistsWithSellerException.class)
	private ResponseEntity<Object> handleStoreAlreadyExistsWithSellerException(StoreAlreadyExistsWithSellerException  exp){
		return structure(HttpStatus.FOUND, exp.getMessage(), "Store Data Already Found for this Seller");
	}
	
	@ExceptionHandler(StoreAddressNotFoundException.class)
	private ResponseEntity<Object> handleStoreAddressNotFoundException(StoreAddressNotFoundException  exp){
		return structure(HttpStatus.NOT_FOUND, exp.getMessage(), "Store address not yet inserted");
	}

	@ExceptionHandler(StoreAlreadyHasAddressException.class)
	private ResponseEntity<Object> handleStoreAlreadyHasAddressException(StoreAlreadyHasAddressException exp){
		return structure(HttpStatus.FOUND, exp.getMessage(), "Address already registered with this Shop");
	}
	
}
