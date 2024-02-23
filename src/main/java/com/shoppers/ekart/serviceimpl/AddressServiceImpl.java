package com.shoppers.ekart.serviceimpl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shoppers.ekart.entity.Address;
import com.shoppers.ekart.entity.Customer;
import com.shoppers.ekart.entity.Seller;
import com.shoppers.ekart.entity.Store;
import com.shoppers.ekart.exception.AddressNotExistsWithThisIdException;
import com.shoppers.ekart.exception.SellerNotHavingStoreException;
import com.shoppers.ekart.exception.StoreAddressNotFoundException;
import com.shoppers.ekart.exception.StoreAlreadyHasAddressException;
import com.shoppers.ekart.exception.StoreNotFoundByIdException;
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
	
	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> addAddress(AddressRequest request) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepo.findByUsername(username).map(user ->{
			Address address = mapToAddress(request);
			switch(user.getUserRole()) {
				case SELLER:
					Seller seller = (Seller)user;
					Store store = seller.getStore();
					if(store!=null) {
						if(store.getAddress()!=null) throw new StoreAlreadyHasAddressException("Failed to Add Address to "+store.getStoreName());
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
					return new ResponseEntity<ResponseStructure<AddressResponse>>(
							structure.setStatusCode(HttpStatus.FOUND.value())
									 .setMessage("Address found with ID: "+addressId)
									 .setData(mapToAddressReponse(address)), HttpStatus.FOUND);
				}).orElseThrow(() -> new AddressNotExistsWithThisIdException("Failed to Fetch this ID"));
	}

	@Override
	public ResponseEntity<ResponseStructure<AddressResponse>> fetchAddressByStore(int storeId) {
		return storeRepo.findById(storeId)
				.map(store ->{
					Address address = store.getAddress();
					if(address==null) throw new StoreAddressNotFoundException("Failed to Fetch Address by Store");
					return new ResponseEntity<ResponseStructure<AddressResponse>>(
							structure.setStatusCode(HttpStatus.FOUND.value())
									 .setMessage("Store Address Found")
									 .setData(mapToAddressReponse(address)), HttpStatus.FOUND);
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
				.build();
	}
}
