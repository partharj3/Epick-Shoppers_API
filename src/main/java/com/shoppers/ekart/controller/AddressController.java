package com.shoppers.ekart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppers.ekart.requestdto.AddressRequest;
import com.shoppers.ekart.responsedto.AddressResponse;
import com.shoppers.ekart.service.AddressService;
import com.shoppers.ekart.util.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173/")
public class AddressController {
	
	private AddressService addressService;
	
	@PostMapping("/stores/{storeId}/addresses")
	private ResponseEntity<ResponseStructure<AddressResponse>> addAddressToStore
							(@PathVariable int storeId,@RequestBody AddressRequest request){
		return addressService.addAddressToStore(storeId,request);
	}
	
	@PostMapping("/customers/{customerId}/addresses")
	private ResponseEntity<ResponseStructure<AddressResponse>> addAddressToCustomer
							(@PathVariable int customerId,@RequestBody AddressRequest request){
		return addressService.addAddressToCustomer(customerId,request);
	}
	
	@PutMapping("/addresses/{addressId}")
	private ResponseEntity<ResponseStructure<AddressResponse>> updateAddress
						(@PathVariable int addressId ,@RequestBody AddressRequest request){
		return addressService.updateAddress(addressId,request);
	}
		
	@GetMapping("/addresses/{addressId}")
	private ResponseEntity<ResponseStructure<AddressResponse>> fetchAddressById(@PathVariable int addressId){
		return addressService.fetchAddressById(addressId);
	}
	
	@GetMapping("/stores/{storeId}/addresses")
	private ResponseEntity<ResponseStructure<AddressResponse>> fetchAddressByStore(@PathVariable int storeId){
		return addressService.fetchAddressByStore(storeId);
	}
}
