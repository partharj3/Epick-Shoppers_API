package com.shoppers.ekart.serviceimpl;

import java.io.IOException;
import java.util.Optional;

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
					storeImg.setContentType(image.getContentType());
					try {
						storeImg.setImageBytes(image.getBytes());
					} catch (IOException e) {
						throw new IllegalRequestException(e.getMessage());
					}
					Image saved = imageRepo.save(storeImg);
					
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
	    return imageRepo.findById(imageId)
	    		.map(image ->{
//	    		    HttpHeaders headers = new HttpHeaders();
//	    		    headers.setContentType(MediaType.IMAGE_JPEG);
	    			 return ResponseEntity.ok()
//	    			    		.headers(headers)
	    			    		.contentType(MediaType.valueOf(image.getContentType()))
	    			    		.contentLength(image.getImageBytes().length)
	    					 	.body(image.getImageBytes());
	    			 
	    		}).orElseThrow(() -> new ImageNotFoundByIdException("Failed to get the image"));	   
	}

	@Override
	public ResponseEntity<byte[]> getImageByStoreId(int storeId) {
		return storeRepo.findById(storeId)
				.map(store ->{
					System.out.println("STORE FOUND: ");
					Optional<Image> optionalImage = imageRepo.findImageByImageTypeAndStoreId(ImageType.LOGO,storeId);
					System.err.println("optional: "+optionalImage);
					if(optionalImage.isPresent()) {
						StoreImage storeImage = (StoreImage)optionalImage.get();
						 return ResponseEntity.ok()
		    			    		.contentType(MediaType.valueOf(storeImage.getContentType()))
		    			    		.contentLength(storeImage.getImageBytes().length)
		    					 	.body(storeImage.getImageBytes());
						}
					else
						throw new IllegalRequestException("STORE FOUND, BUT NOT IMAGE");
				})
				.orElseThrow(() -> new StoreNotFoundByIdException("Failed to fetch store-logo"));
	}

}
