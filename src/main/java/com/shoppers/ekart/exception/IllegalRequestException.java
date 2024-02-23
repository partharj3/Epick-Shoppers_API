package com.shoppers.ekart.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IllegalRequestException extends RuntimeException {
	private String message;
}
