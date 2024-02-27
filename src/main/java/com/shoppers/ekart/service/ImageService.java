package com.shoppers.ekart.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.shoppers.ekart.util.ResponseStructure;

public interface ImageService {

	ResponseEntity<ResponseStructure<String>> addStoreImage(int storeId, MultipartFile image);

	ResponseEntity<byte[]> getImage(String imageId);

	ResponseEntity<byte[]> getImageByStoreId(int storeId);

}
