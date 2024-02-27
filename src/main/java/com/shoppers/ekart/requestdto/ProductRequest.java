package com.shoppers.ekart.requestdto;

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
public class ProductRequest {
	private String productName;
	private String productDescription;
	private double productPrice;
	private int productQuantity;
}
