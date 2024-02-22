package com.shoppers.ekart.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.shoppers.ekart.requestdto.StoreRequest;
import com.shoppers.ekart.responsedto.StoreResponse;
import com.shoppers.ekart.util.ResponseStructure;

import jakarta.validation.Valid;

public interface StoreService {
	ResponseEntity<ResponseStructure<StoreResponse>> addStore(@Valid StoreRequest request);
	ResponseEntity<ResponseStructure<StoreResponse>> updateStore(int storeId, @Valid StoreRequest request);
	ResponseEntity<ResponseStructure<List<StoreResponse>>> fetchAllStores();
	ResponseEntity<ResponseStructure<StoreResponse>> fetchStoreById(int storeId);
	ResponseEntity<ResponseStructure<StoreResponse>> fetchStoreBySeller(int sellerId);

}
