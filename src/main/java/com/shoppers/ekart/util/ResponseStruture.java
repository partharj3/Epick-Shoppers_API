package com.shoppers.ekart.util;

import org.springframework.stereotype.Component;

@Component
public class ResponseStruture<T> {
	private int statusCode;
	private String message;
	private T data;
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public ResponseStruture<T> setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}
	public String getMessage() {
		return message;
	}
	public ResponseStruture<T> setMessage(String message) {
		this.message = message;
		return this;
	}
	public T getData() {
		return data;
	}
	public ResponseStruture<T> setData(T data) {
		this.data = data;
		return this;
	}	
}
