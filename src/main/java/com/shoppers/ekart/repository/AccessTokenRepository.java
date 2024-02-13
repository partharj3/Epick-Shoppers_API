package com.shoppers.ekart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppers.ekart.entity.AccessToken;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long>{

	Optional<AccessToken> findByToken(String token);
	
}
