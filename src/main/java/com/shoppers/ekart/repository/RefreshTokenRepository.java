package com.shoppers.ekart.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppers.ekart.entity.RefreshToken;
import com.shoppers.ekart.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

	Optional<RefreshToken> findByToken(String rt);
	List<RefreshToken> findByUserAndIsBlockedAndTokenNot(User user,boolean isBlocked,String refreshToken);
	List<RefreshToken> findAllByExpirationBefore(LocalDateTime now);
	List<RefreshToken> findByTokenAndIsBlocked(User user, boolean b);
}
