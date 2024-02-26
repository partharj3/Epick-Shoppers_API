package com.shoppers.ekart.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shoppers.ekart.entity.Seller;
import com.shoppers.ekart.entity.Store;
import com.shoppers.ekart.enums.UserRole;
import com.shoppers.ekart.exception.IllegalRequestException;
import com.shoppers.ekart.exception.NoStoreDataExistsException;
import com.shoppers.ekart.exception.SellerNotFoundByThisIdException;
import com.shoppers.ekart.exception.StoreAlreadyExistsWithSellerException;
import com.shoppers.ekart.exception.StoreNotFoundByIdException;
import com.shoppers.ekart.exception.UsernameNotFoundException;
import com.shoppers.ekart.repository.SellerRepository;
import com.shoppers.ekart.repository.StoreRepository;
import com.shoppers.ekart.repository.UserRepository;
import com.shoppers.ekart.requestdto.StoreRequest;
import com.shoppers.ekart.responsedto.StoreResponse;
import com.shoppers.ekart.service.StoreService;
import com.shoppers.ekart.util.ResponseStructure;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StoreServiceImpl implements StoreService{
	
	private StoreRepository storeRepo;
	private ResponseStructure<StoreResponse> response;
	private UserRepository userRepo;
	private SellerRepository sellerRepo;
	
	@Override
	public ResponseEntity<ResponseStructure<StoreResponse>> addStore(StoreRequest request) {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if(username==null) throw new UsernameNotFoundException("Failed to Add Store");
		System.out.println("----entered---");
		return userRepo.findByUsernameAndUserRole(username,UserRole.SELLER).map(user->{	
			Seller seller = (Seller)user;
			Store store = mapToStore(request);
			
			if(seller.getStore()!=null) throw new StoreAlreadyExistsWithSellerException("Failed to Add Store");	
			store = storeRepo.save(store);
			
			seller.setStore(store);
			sellerRepo.save(seller);
			return new ResponseEntity<ResponseStructure<StoreResponse>>(
					   response.setStatusCode(HttpStatus.CREATED.value())
								.setMessage("Store Data Uploaded Successfully")
								.setData(mapToStoreResponse(store)), HttpStatus.CREATED);
			
		}).orElseThrow(()-> new IllegalRequestException("User not Valid to Create Store"));
	}

	@Override
	public ResponseEntity<ResponseStructure<StoreResponse>> updateStore(int storeId, StoreRequest request) {
		
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		if(username==null) throw new UsernameNotFoundException("Failed to update Store");
		
		return storeRepo.findById(storeId)
				.map(store ->{
					Store updatedStore = mapToStore(request);
					updatedStore.setStoreId(store.getStoreId());
					storeRepo.save(updatedStore);
					
					return new ResponseEntity<ResponseStructure<StoreResponse>>(
							response.setStatusCode(HttpStatus.OK.value())
									.setData(mapToStoreResponse(updatedStore))
									.setMessage("Store Data updated Successfully"), HttpStatus.OK);
		
		}).orElseThrow(()-> new StoreNotFoundByIdException("Failed to update the Store"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<StoreResponse>>> fetchAllStores() {
		List<Store> storeList = storeRepo.findAll();
		if(!storeList.isEmpty()) {
			
			List<StoreResponse> responseList = new ArrayList<>();
			
			for(Store store:storeList) 
				responseList.add(mapToStoreResponse(store));
			
			ResponseStructure<List<StoreResponse>> structure = new ResponseStructure<>();
			
			return new ResponseEntity<ResponseStructure<List<StoreResponse>>>(
					structure.setStatusCode(HttpStatus.FOUND.value())
							.setMessage("Store List Found")
							.setData(responseList), HttpStatus.FOUND);
		}
		throw new NoStoreDataExistsException("Failed to fetch Store Data");
	}

	@Override
	public ResponseEntity<ResponseStructure<StoreResponse>> fetchStoreById(int storeId) {
		/** Public Method. No need to Login **/
		return storeRepo.findById(storeId)
			.map(store ->{
				return ResponseEntity.ok(
						response.setStatusCode(HttpStatus.OK.value())
						.setMessage("Store Information found by Seller")
						.setData(mapToStoreResponse(store)));
			})
			.orElseThrow(()-> new StoreNotFoundByIdException("Failed to Fetch store by ID"));
	}

	@Override
	public ResponseEntity<ResponseStructure<StoreResponse>> fetchStoreBySeller(int sellerId) {
		return sellerRepo.findById(sellerId).map(seller ->{
			Store store = seller.getStore();
			if(store==null) throw new NoStoreDataExistsException("No Store for Seller "+seller.getUsername());
			
			return ResponseEntity.ok(
					response.setStatusCode(HttpStatus.OK.value())
							.setMessage("Store Information found by Seller")
							.setData(mapToStoreResponse(store)));
		}).orElseThrow(()-> new SellerNotFoundByThisIdException("Failed to fetch Store"));
	}
	
	private StoreResponse mapToStoreResponse(Store store) {
		return StoreResponse.builder()
				.storeId(store.getStoreId())
				.storeName(store.getStoreName())
				.about(store.getAbout())
				.build();
	}

	private Store mapToStore(@Valid StoreRequest request) {
		return Store.builder()
				.storeName(request.getStoreName())
				.about(request.getAbout())
				.build();
	}
}


















