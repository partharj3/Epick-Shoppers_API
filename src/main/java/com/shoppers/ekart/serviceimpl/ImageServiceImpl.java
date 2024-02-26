package com.shoppers.ekart.serviceimpl;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.shoppers.ekart.entity.Image;
import com.shoppers.ekart.entity.StoreImage;
import com.shoppers.ekart.enums.ImageType;
import com.shoppers.ekart.exception.IllegalRequestException;
import com.shoppers.ekart.exception.ImageNotFoundByIdException;
import com.shoppers.ekart.exception.StoreNotFoundByIdException;
import com.shoppers.ekart.repository.ImageRepository;
import com.shoppers.ekart.repository.StoreRepository;
import com.shoppers.ekart.service.ImageService;
import com.shoppers.ekart.util.ResponseStructure;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService{

	private ImageRepository imageRepo;
	private StoreRepository storeRepo; 
	
	@Override
	public ResponseEntity<ResponseStructure<String>> addStoreImage(int storeId, MultipartFile image) {
		return storeRepo.findById(storeId)
				.map(store ->{
					StoreImage storeImg = new StoreImage();
					storeImg.setStoreId(storeId);
					storeImg.setImageType(ImageType.LOGO);
					try {
						storeImg.setImageBytes(image.getBytes());
					} catch (IOException e) {
						throw new IllegalRequestException(e.getMessage());
					}
					System.out.println(storeImg);
					Image saved = imageRepo.save(storeImg);
					System.out.println(saved);
					
					ResponseStructure<String> structure = new ResponseStructure<>();
					
					return new ResponseEntity<ResponseStructure<String>>(
							structure.setStatusCode(HttpStatus.CREATED.value())
									 .setMessage("Store Image uploaded Succesfully !!")
									 .setData("/api/v1/images/"+saved.getImageId())
							,HttpStatus.CREATED);
				})
				.orElseThrow(() -> new StoreNotFoundByIdException("Failed to add image to Store"));
	}

	@Override
	public ResponseEntity<byte[]> getImage(String imageId) {
	    Image image = imageRepo.findById(imageId).orElseThrow(() -> new ImageNotFoundByIdException("Failed to get the image"));

	    byte[] imageBytes = image.getImageBytes();

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.IMAGE_JPEG);

//	    ResponseStructure<byte[]> structure = new ResponseStructure<>();
//	    structure.setData(imageBytes);
//	    structure.setMessage("Image found");
//	    structure.setStatusCode(HttpStatus.OK.value());

	    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	}
}
