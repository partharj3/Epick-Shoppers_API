package com.shoppers.ekart.entity;

import com.shoppers.ekart.enums.AvailabilityStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int productId;
	private String productName;
	private String productDescription;
	private double productPrice;
	private int productQuantity;
	
	private AvailabilityStatus status;
	private double avgRating;
	private int totalOrders;
	
	@ManyToOne
	private Store store;
}
