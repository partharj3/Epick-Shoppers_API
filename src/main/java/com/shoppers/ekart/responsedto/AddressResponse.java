package com.shoppers.ekart.responsedto;

import java.util.List;

import com.shoppers.ekart.entity.Contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {
	private int addressId;
	private String streetAddress;
	private String streetAddressAdditional;
	private String city;
	private String state;
	private String country;
	private int pincode;
	private String addressType;
	private List<Contact> contacts;
}
