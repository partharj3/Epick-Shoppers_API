package com.shoppers.ekart.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;

@Component
public class CookieManager {
	
	@Value("${myapp.domain}")
	private String domain;
	
	/**
	 * This method could be written in AuthService itself, and made it as Private. 
	 * 
	 * But if this method is public, we could access anywhere it is needed
	 * 
	 * @param cookie
	 * @param data
	 * @param expirationInSeconds
	 * @return
	 */
	
	public Cookie configure(Cookie cookie, int expirationInSeconds) {
		cookie.setDomain(domain);
		cookie.setHttpOnly(true); // only server side could access it not the client side.
		cookie.setSecure(false);
		cookie.setPath("/");
		cookie.setMaxAge(expirationInSeconds);
		return cookie;
	}
	
	/**
	 * If the user wants to remove his cookie by himself
	 * then this method should do that operation.
	 */
	
	public Cookie invalidate(Cookie cookie) {
		cookie.setPath("/");
		cookie.setMaxAge(0);
		return cookie;
	}
	
	
	
	
	
	
	
	
	
	
}
