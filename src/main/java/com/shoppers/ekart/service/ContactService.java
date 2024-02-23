package com.shoppers.ekart.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.shoppers.ekart.requestdto.ContactRequest;
import com.shoppers.ekart.responsedto.ContactResponse;
import com.shoppers.ekart.util.ResponseStructure;

public interface ContactService {

	ResponseEntity<ResponseStructure<ContactResponse>> addContact(int addressId, ContactRequest request);

	ResponseEntity<ResponseStructure<ContactResponse>> updateContact(int contactId, ContactRequest request);

	ResponseEntity<ResponseStructure<ContactResponse>> fetchContactById(int contactId);

	ResponseEntity<ResponseStructure<List<ContactResponse>>> fetchContactsByAddress(int addressId);
}
