package com.shoppers.ekart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppers.ekart.requestdto.ProductRequest;
import com.shoppers.ekart.responsedto.ProductResponse;
import com.shoppers.ekart.service.ProductService;
import com.shoppers.ekart.util.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class ProductController {

	private ProductService productService;
	
	@PostMapping("/stores/{storeId}/products")
	public ResponseEntity<ResponseStructure<ProductResponse>> addProduct(@PathVariable int storeId , @RequestBody ProductRequest request){
		return productService.addProduct(storeId,request);
	}
	
}
