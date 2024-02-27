package com.shoppers.ekart.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductResponse {
	private int productId;
	private String productName;
	private String productDescription;
	private double productPrice;
	private int productQuantity;
	
	private String status;
	private double avgRating;
	private int totalOrders;
}
