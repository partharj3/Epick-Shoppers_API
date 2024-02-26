package com.shoppers.ekart.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shoppers.ekart.entity.Address;
import com.shoppers.ekart.entity.Customer;
import com.shoppers.ekart.entity.Seller;
import com.shoppers.ekart.entity.Store;
import com.shoppers.ekart.enums.AddressType;
import com.shoppers.ekart.exception.AddressNotExistsWithThisIdException;
import com.shoppers.ekart.exception.CustomerAddressLimitExceededException;
import com.shoppers.ekart.exception.SellerNotHavingStoreException;
import com.shoppers.ekart.exception.StoreAddressNotFoundException;
import com.shoppers.ekart.exception.StoreAlreadyHasAddressException;
import com.shoppers.ekart.exception.StoreNotFoundByIdException;
import com.shoppers.ekart.exception.UserNotFoundByIdException;
import com.shoppers.ekart.exception.UsernameNotFoundException;
import com.shoppers.ekart.repository.AddressRepository;
import com.shoppers.ekart.repository.CustomerRepository;
import com.shoppers.ekart.repository.StoreRepository;
import com.shoppers.ekart.repository.UserRepository;
import com.shoppers.ekart.requestdto.AddressRequest;
import com.shoppers.ekart.responsedto.AddressResponse;
import com.shoppers.ekart.service.AddressService;
import com.shoppers.ekart.util.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AddressServiceImpl implements AddressService{
	
	private AddressRepository addressRepo;
	private UserRepository userRepo;
	private StoreRepository storeRepo;
	private CustomerRepository customerRepo;
	private ResponseStructure<AddressResponse> structure;
	
	public ResponseEntity<ResponseStructure<AddressResponse>> addAddress(AddressRequest request) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepo.findByUsername(username).map(user ->{
			Address address = mapToAddress(request);
			switch(user.getUserRole()) {
				case SELLER:
					Seller seller = (Seller)user;
					Store store = seller.getStore();
					if(store!=null) {
						if(store.getAddress()!=null) 
							throw new StoreAlreadyHasAddressException("Failed to Add Address to "+store.getStoreName());
						
						store.setAddress(address);
						addressRepo.save(address);
						
						store.setStoreId(store.getStoreId());
						storeRepo.save(store);
					}
					else {
						throw new SellerNotHavingStoreException("Failed to Add Address");
					}
					break;
				case CUSTOMER:
					Customer customer = (Customer)user;
					address.setCustomer(customer);
					addressRepo.save(address);
					
					customer.getAddresses().add(address);
					customerRepo.save(customer);
			}
			return new ResponseEntity<ResponseStructure<AddressResponse>>(
					structure.setStatusCode(HttpStatus.CREATED.value())
							 .setMessage("Address added to "+user.getUserRole())
							 .setData(mapToAddressReponse(address)), HttpStatus.CREATED);
		})
		.orElseThrow(() -> new UsernameNotFoundException("Failed tp Add Address"));
	}

	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> addAddressToStore(int storeId, AddressRequest request) {
		return storeRepo.findById(storeId)
			.map(store ->{
				if(store.getAddress()==null) {
					Address address = mapToAddress(request);
					addressRepo.save(address);
					store.setAddress(address);
					storeRepo.save(store);
					
					return new ResponseEntity<ResponseStructure<AddressResponse>>(
							structure.setStatusCode(HttpStatus.CREATED.value())
									 .setMessage("Address added to "+store.getStoreName())
									 .setData(mapToAddressReponse(address)), HttpStatus.CREATED);
				}
				else
					throw new StoreAlreadyHasAddressException("Failed to add Address to Store");
			})
			.orElseThrow(()-> new StoreNotFoundByIdException("Failed to Add Address to Store "));
	}

	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> addAddressToCustomer(int customerId,
			AddressRequest request) {
		return customerRepo.findById(customerId)
				.map(customer ->{
					if(customer.getAddresses().size()<5) {
						Address address = mapToAddress(request);
						address.setCustomer(customer);
						addressRepo.save(address);
						
						customer.getAddresses().add(address);
						customer.setUserId(customerId);
						customerRepo.save(customer);
						
						return new ResponseEntity<ResponseStructure<AddressResponse>>(
								structure.setStatusCode(HttpStatus.CREATED.value())
										 .setMessage("Address added to "+customer.getUsername())
										 .setData(mapToAddressReponse(address)), HttpStatus.CREATED);
					}
					else
						throw new CustomerAddressLimitExceededException("Failed to add address");
				})
				.orElseThrow(() -> new UserNotFoundByIdException("Failed to Add Address to Customer"));
	}

	public ResponseEntity<ResponseStructure<AddressResponse>> updateAddress(int addressId, AddressRequest request) {
//		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return addressRepo.findById(addressId)
				.map(address ->{
					Address updatedAddress = mapToAddress(request);
					updatedAddress.setAddressId(address.getAddressId());
					addressRepo.save(updatedAddress);
					
					return new ResponseEntity<ResponseStructure<AddressResponse>>(
							structure.setStatusCode(HttpStatus.OK.value())
									 .setMessage("Address Updated Successfully")
									 .setData(mapToAddressReponse(address)), HttpStatus.OK);
				})
				.orElseThrow(() -> new AddressNotExistsWithThisIdException("Failed to Update"));
	}
	
	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> fetchAddressById(int addressId) {
		return addressRepo.findById(addressId)
				.map(address ->{
					return ResponseEntity.ok(
							structure.setStatusCode(HttpStatus.OK.value())
							 .setMessage("Store Address Found")
							 .setData(mapToAddressReponse(address)));
				}).orElseThrow(() -> new AddressNotExistsWithThisIdException("Failed to Fetch this ID"));
	}

	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> fetchAddressByStore(int storeId) {
		return storeRepo.findById(storeId)
				.map(store ->{
					Address address = store.getAddress();
					if(address==null) throw new StoreAddressNotFoundException("Failed to Fetch Address by Store");
					return ResponseEntity.ok(
							structure.setStatusCode(HttpStatus.OK.value())
									 .setMessage("Store Address Found")
									 .setData(mapToAddressReponse(address)));
				})
				.orElseThrow(() -> new StoreNotFoundByIdException("Failed to Fetch Address of Store"));
	}
	
	private AddressResponse mapToAddressReponse(Address address) {
		return AddressResponse.builder()
				.addressId(address.getAddressId())
				.streetAddress(address.getStreetAddress())
				.streetAddressAdditional(address.getStreetAddressAdditional())
				.city(address.getCity())
				.state(address.getState())
				.country(address.getCountry())
				.pincode(address.getPincode())
				.addressType(address.getAddressType().name())
				.contacts(address.getContacts())
				.build();
	}

	private Address mapToAddress(AddressRequest request) {
		return Address.builder()
				.streetAddress(request.getStreetAddress())
				.streetAddressAdditional(request.getStreetAddressAdditional())
				.city(request.getCity())
				.state(request.getState())
				.country(request.getCountry())
				.pincode(request.getPincode())
				.addressType(AddressType.valueOf(request.getAddressType()))
				.build();
	}
}
