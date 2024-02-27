package com.shoppers.ekart.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.shoppers.ekart.entity.Product;
import com.shoppers.ekart.enums.AvailabilityStatus;
import com.shoppers.ekart.exception.StoreNotFoundByIdException;
import com.shoppers.ekart.repository.ProductRepository;
import com.shoppers.ekart.repository.StoreRepository;
import com.shoppers.ekart.requestdto.ProductRequest;
import com.shoppers.ekart.responsedto.ProductResponse;
import com.shoppers.ekart.service.ProductService;
import com.shoppers.ekart.util.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService{
	
	private ProductRepository productRepo;
	private StoreRepository storeRepo;
	private ResponseStructure<ProductResponse> structure;
	
	@Override
	public ResponseEntity<ResponseStructure<ProductResponse>> addProduct(int storeId, ProductRequest request) {
		return storeRepo.findById(storeId)
				.map(store ->{
					Product product = mapToProduct(request);
					product.setStore(store);
					productRepo.save(product);
					
					return new ResponseEntity<ResponseStructure<ProductResponse>>(
							structure.setStatusCode(HttpStatus.OK.value())
							.setMessage("Product added to store "+store.getStoreName())
							.setData(mapToProductResponse(product)), HttpStatus.OK);					
				})
				.orElseThrow(()-> new StoreNotFoundByIdException("Failed to add product to Store"));
	}

	private ProductResponse mapToProductResponse(Product product) {
		return ProductResponse.builder()
				.productId(product.getProductId())
				.productName(product.getProductName())
				.productDescription(product.getProductDescription())
				.productPrice(product.getProductPrice())
				.productQuantity(product.getProductQuantity())
				.avgRating(product.getAvgRating())
				.totalOrders(product.getTotalOrders())
				.status(product.getStatus().name())
				.build();
	}

	private Product mapToProduct(ProductRequest request) {
		return Product.builder()
				.productName(request.getProductName())
				.productDescription(request.getProductDescription())
				.productPrice(request.getProductPrice())
				.productQuantity(request.getProductQuantity())
				.status(AvailabilityStatus.AVAILABLE)
				.build();
	}

	
	
}
