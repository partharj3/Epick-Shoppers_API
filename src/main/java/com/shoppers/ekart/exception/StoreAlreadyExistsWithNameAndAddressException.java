package com.shoppers.ekart.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StoreAlreadyExistsWithNameAndAddressException extends RuntimeException {
	private String message;
}
