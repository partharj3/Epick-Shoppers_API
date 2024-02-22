package com.shoppers.ekart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppers.ekart.entity.User;
import com.shoppers.ekart.enums.UserRole;

public interface UserRepository extends JpaRepository<User, Integer>{
	boolean existsByEmail(String email);
	
	List<User> findByUserRole(UserRole role);
	
	Optional<User> findByEmail(String email);
	
	Optional<User> findByUsername(String username);
	
	List<User> findByIsEmailVerifiedFalse();
	
	void deleteByIsEmailVerifiedFalse();

	Optional<User> findByUsernameAndUserRole(String username,UserRole role);
	
}

