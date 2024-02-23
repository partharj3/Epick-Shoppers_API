package com.shoppers.ekart.util;

import java.util.Date;

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
public class MessageStructure {

	private String to;
	private String subject;
	private Date sentDate; // Even though the Date is out-dated than LocalDate, mail dependency classes accept Date object.
	private String text;
	
}
