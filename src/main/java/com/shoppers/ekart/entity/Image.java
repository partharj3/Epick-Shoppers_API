package com.shoppers.ekart.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.shoppers.ekart.enums.ImageType;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "image")
@Getter
@Setter
public class Image {
	@Id
	private String imageId;
	private ImageType imageType;
	private byte[] imageBytes;
}
