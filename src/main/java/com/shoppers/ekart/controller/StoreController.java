package com.shoppers.ekart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppers.ekart.requestdto.StoreRequest;
import com.shoppers.ekart.responsedto.StoreResponse;
import com.shoppers.ekart.service.StoreService;
import com.shoppers.ekart.util.ResponseStructure;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173/")
public class StoreController {
	
	private StoreService storeService;
	
	@PreAuthorize("hasAuthority('SELLER')")
	@PostMapping("/stores")
	public ResponseEntity<ResponseStructure<StoreResponse>> addStore(@RequestBody StoreRequest request){
		return storeService.addStore(request);
	}
	
	@PreAuthorize("hasAuthority('SELLER')")
	@PutMapping("/stores/{storeId}")
	public ResponseEntity<ResponseStructure<StoreResponse>> updateStore(@RequestBody @Valid StoreRequest request, @PathVariable int storeId){
		return storeService.updateStore(storeId,request);
	}
	
	@GetMapping("/stores")
	public ResponseEntity<ResponseStructure<List<StoreResponse>>> fetchAllStores(){
		return storeService.fetchAllStores();
	}
	
	@GetMapping("/stores/{storeId}")
	public ResponseEntity<ResponseStructure<StoreResponse>> fetchStoreById(@PathVariable int storeId){
		return storeService.fetchStoreById(storeId);
	}
	
	@GetMapping("/sellers/{sellerId}/stores")
	public ResponseEntity<ResponseStructure<StoreResponse>> fetchStoreBySeller(@PathVariable int sellerId){
		return storeService.fetchStoreBySeller(sellerId);
	}
	
}
