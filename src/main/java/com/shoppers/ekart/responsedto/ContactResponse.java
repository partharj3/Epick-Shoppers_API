package com.shoppers.ekart.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ContactResponse {
	private int contactId;
	private String contactName;
	private long contactNumber;
	private String priority;
}
