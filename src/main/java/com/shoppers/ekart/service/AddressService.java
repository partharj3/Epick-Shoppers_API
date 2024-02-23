package com.shoppers.ekart.service;

import org.springframework.http.ResponseEntity;

import com.shoppers.ekart.requestdto.AddressRequest;
import com.shoppers.ekart.responsedto.AddressResponse;
import com.shoppers.ekart.util.ResponseStructure;

public interface AddressService {

	ResponseEntity<ResponseStructure<AddressResponse>> addAddress(AddressRequest request);

	ResponseEntity<ResponseStructure<AddressResponse>> updateAddress(int addressId, AddressRequest request);

	ResponseEntity<ResponseStructure<AddressResponse>> fetchAddressById(int addressId);

	ResponseEntity<ResponseStructure<AddressResponse>> fetchAddressByStore(int storeId);

}
