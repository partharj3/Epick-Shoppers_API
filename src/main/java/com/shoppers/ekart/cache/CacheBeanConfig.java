package com.shoppers.ekart.cache;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.shoppers.ekart.entity.User;

@Configuration
public class CacheBeanConfig {

	@Bean
	public CacheStore<User> useCacheStore(){ 
		return new CacheStore<User>(Duration.ofMinutes(3)); 
	}
	
	@Bean
	public CacheStore<Integer> otpCacheStore(){
		return new CacheStore<Integer>(Duration.ofMinutes(2));
	}
	
}
