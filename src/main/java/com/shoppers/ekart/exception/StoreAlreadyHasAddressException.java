package com.shoppers.ekart.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StoreAlreadyHasAddressException extends RuntimeException {
	private String message;
}
