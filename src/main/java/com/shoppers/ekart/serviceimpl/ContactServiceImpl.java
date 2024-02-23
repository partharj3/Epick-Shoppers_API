package com.shoppers.ekart.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.shoppers.ekart.entity.Contact;
import com.shoppers.ekart.enums.Priority;
import com.shoppers.ekart.exception.AddressNotExistsWithThisIdException;
import com.shoppers.ekart.exception.ContactInfoNotFoundByIdException;
import com.shoppers.ekart.exception.ContactNumberAlreadyFoundException;
import com.shoppers.ekart.exception.IllegalRequestException;
import com.shoppers.ekart.exception.NoContactsExistsForThisAddressException;
import com.shoppers.ekart.repository.AddressRepository;
import com.shoppers.ekart.repository.ContactRepository;
import com.shoppers.ekart.requestdto.ContactRequest;
import com.shoppers.ekart.responsedto.ContactResponse;
import com.shoppers.ekart.service.ContactService;
import com.shoppers.ekart.util.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ContactServiceImpl implements ContactService{
	
	private ContactRepository contactRepo;
	private AddressRepository addressRepo;
	private ResponseStructure<ContactResponse> structure;
	
	@Override
	public ResponseEntity<ResponseStructure<ContactResponse>> addContact(int addressId, ContactRequest request) {
		if(contactRepo.existsByContactNumber(request.getContactNumber())) 
			throw new ContactNumberAlreadyFoundException("Failed to add contact");
		return addressRepo.findById(addressId)
			.map(address ->{				
				if(address.getContacts().size()<2 || address.getContacts()==null) {
					Contact contact = mapToContact(request);
					contact.setAddress(address);
					contactRepo.save(contact);
					
					address.getContacts().add(contact);
					addressRepo.save(address);
					
					return new ResponseEntity<ResponseStructure<ContactResponse>>(
							structure.setStatusCode(HttpStatus.CREATED.value())
									 .setData(mapToContactResponse(contact))
									 .setMessage("Contact Information added Successfully"), HttpStatus.CREATED);
				}
				throw new IllegalRequestException("Only 2 Contact Informations allowed");
			})
			.orElseThrow(() -> new AddressNotExistsWithThisIdException("Failed to add Contact"));
	}

	@Override
	public ResponseEntity<ResponseStructure<ContactResponse>> updateContact(int contactId, ContactRequest request) {
		return contactRepo.findById(contactId)
				.map(contact ->{
					Contact updated = mapToContact(request);
					updated.setContactId(contact.getContactId());
					updated.setAddress(contact.getAddress());
					contactRepo.save(updated);
					return new ResponseEntity<ResponseStructure<ContactResponse>>(
							structure.setStatusCode(HttpStatus.OK.value())
									 .setMessage("Contact Information updated Successfully!")
									 .setData(mapToContactResponse(contact)), HttpStatus.OK);
				})
				.orElseThrow(()-> new ContactInfoNotFoundByIdException("Failed to Update Contact Inforamtion"));
	}

	@Override
	public ResponseEntity<ResponseStructure<ContactResponse>> fetchContactById(int contactId) {
		return contactRepo.findById(contactId)
			.map(contact ->{
				return new ResponseEntity<ResponseStructure<ContactResponse>>(
						structure.setStatusCode(HttpStatus.FOUND.value())
								 .setMessage("Contact Information Found!")
								 .setData(mapToContactResponse(contact)), HttpStatus.FOUND);
			})
			.orElseThrow(() -> new ContactInfoNotFoundByIdException("Failed to fetch Contact"));
	}

	@Override
	public ResponseEntity<ResponseStructure<List<ContactResponse>>> fetchContactsByAddress(int addressId) {
		return addressRepo.findById(addressId)
			.map(address ->{
				List<Contact> contacts = address.getContacts();
				if(contacts.isEmpty() || contacts==null) 
					throw new NoContactsExistsForThisAddressException("Failed to fetch contacts for address for "+address.getAddressId());
					
					List<ContactResponse> responses = new ArrayList<>(); 
					for(Contact contact:contacts) 
						responses.add(mapToContactResponse(contact));
					
				ResponseStructure<List<ContactResponse>> structure = new ResponseStructure<>();
					
				return new ResponseEntity<ResponseStructure<List<ContactResponse>>>(structure
						.setData(responses)
						.setMessage("Contact List found for Address")
						.setStatusCode(HttpStatus.FOUND.value()), HttpStatus.FOUND);
			})
			.orElseThrow(()-> new AddressNotExistsWithThisIdException("Failed to fetch Contact"));
	}
	
	private ContactResponse mapToContactResponse(Contact contact) {
		return ContactResponse.builder()
					.contactId(contact.getContactId())
					.contactName(contact.getContactName())
					.contactNumber(contact.getContactNumber())
					.priority(contact.getPriority().name())
//					.address(contact.getAddress())
					.build();
	}

	private Contact mapToContact(ContactRequest request) {
		return Contact.builder()
					  .contactName(request.getContactName())
					  .contactNumber(request.getContactNumber())
					  .priority(Priority.valueOf(request.getPriority()))
					  .build();
	}
}
