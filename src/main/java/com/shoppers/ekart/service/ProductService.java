package com.shoppers.ekart.service;

import org.springframework.http.ResponseEntity;

import com.shoppers.ekart.requestdto.ProductRequest;
import com.shoppers.ekart.responsedto.ProductResponse;
import com.shoppers.ekart.util.ResponseStructure;

public interface ProductService {

	ResponseEntity<ResponseStructure<ProductResponse>> addProduct(int storeId, ProductRequest request);

}
