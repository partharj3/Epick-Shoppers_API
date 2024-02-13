package com.shoppers.ekart.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="AccessTokens")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class AccessToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long tokenId; // long because it may exceed the (int) level
	private String token;
	private boolean isBlocked;
	private LocalDateTime expiration;
	
	@ManyToOne
	private User user;
}
