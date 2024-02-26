package com.shoppers.ekart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shoppers.ekart.service.ImageService;
import com.shoppers.ekart.util.ResponseStructure;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1")
public class ImageController {

	private ImageService imageService;
	
	@PostMapping("/stores/{storeId}/images")
	public ResponseEntity<ResponseStructure<String>> addStoreImage(@PathVariable int storeId, MultipartFile image){
		return imageService.addStoreImage(storeId,image);
	}
	
	@GetMapping("/images/{imageId}")
	public ResponseEntity<byte[]> getImage(@PathVariable String imageId){
		return imageService.getImage(imageId);
	}
	
}
