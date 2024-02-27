package com.shoppers.ekart.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document
public class StoreImage extends Image{
	private int storeId;
}
