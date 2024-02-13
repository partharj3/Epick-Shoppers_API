package com.shoppers.ekart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppers.ekart.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>{

	Optional<RefreshToken> findByToken(String rt);

}
