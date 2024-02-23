package com.shoppers.ekart.requestdto;

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
public class AddressRequest {
	private String streetAddress;
	private String streetAddressAdditional;
	private String city;
	private String state;
	private String country;
	private int pincode;
	
}
