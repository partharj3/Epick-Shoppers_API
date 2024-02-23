package com.shoppers.ekart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppers.ekart.requestdto.ContactRequest;
import com.shoppers.ekart.responsedto.ContactResponse;
import com.shoppers.ekart.service.ContactService;
import com.shoppers.ekart.util.ResponseStructure;

@RestController
@RequestMapping("/api/v1")
public class ContactController {
	
	private ContactService contactService;
	
//	@PostMapping("/contacts")
//	public ResponseEntity<ResponseStructure<ContactResponse>> addContact(@RequestBody ContactRequest request){
//		return contactService.addContact(request);
//	}
//	
//	@PutMapping("/contacts/{contactId}")
//	public ResponseEntity<ResponseStructure<ContactResponse>> updateContact(int contactId,@RequestBody ContactRequest request){
//		return contactService.updateContact(contactId,request);
//	}
//	
//	@GetMapping("/contacts/{contactId}")
//	public ResponseEntity<ResponseStructure<ContactResponse>> fetchContactById(int contactId){
//		return contactService.fetchContactById(contactId);
//	}
//	
//	@GetMapping("/addresses/{addressId}/contacts")
//	public ResponseEntity<ResponseStructure<ContactResponse>> fetchContactsByAddress(int addressId){
//		return contactService.fetchContactsByAddress(addressId);
//	}

	
}
