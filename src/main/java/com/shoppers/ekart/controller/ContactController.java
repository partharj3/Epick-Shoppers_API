package com.shoppers.ekart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppers.ekart.requestdto.ContactRequest;
import com.shoppers.ekart.responsedto.ContactResponse;
import com.shoppers.ekart.service.ContactService;
import com.shoppers.ekart.util.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:5173/")
public class ContactController {
	
	private ContactService contactService;
	
	@PostMapping("/addresses/{addressId}/contacts")
	public ResponseEntity<ResponseStructure<ContactResponse>> addContact(@PathVariable int addressId,@RequestBody ContactRequest request){
		return contactService.addContact(addressId,request);
	}
	
	@PutMapping("/contacts/{contactId}")
	public ResponseEntity<ResponseStructure<ContactResponse>> updateContact(@PathVariable int contactId,@RequestBody ContactRequest request){
		return contactService.updateContact(contactId,request);
	}
	
	@GetMapping("/contacts/{contactId}")
	public ResponseEntity<ResponseStructure<ContactResponse>> fetchContactById(@PathVariable int contactId){
		return contactService.fetchContactById(contactId);
	}
	
	@GetMapping("/addresses/{addressId}/contacts")
	public ResponseEntity<ResponseStructure<List<ContactResponse>>> fetchContactsByAddress(@PathVariable int addressId){
		return contactService.fetchContactsByAddress(addressId);
	}

	
}
