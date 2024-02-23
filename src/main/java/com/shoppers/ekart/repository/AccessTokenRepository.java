package com.shoppers.ekart.repository;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List; 

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppers.ekart.entity.AccessToken;
import com.shoppers.ekart.entity.User;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Long>{

	Optional<AccessToken> findByToken(String token);
	
	List<AccessToken> findByUserAndIsBlockedAndTokenNot(User user,boolean isBlocked,String accessToken);

	Optional<AccessToken> findByTokenAndIsBlocked(String at, boolean isBlocked);

	List<AccessToken> findAllByExpirationBefore(LocalDateTime now);

	List<AccessToken> findByTokenAndIsBlocked(User user, boolean isBlocked);

	
}
