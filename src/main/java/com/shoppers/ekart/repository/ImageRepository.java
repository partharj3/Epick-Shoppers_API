package com.shoppers.ekart.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.shoppers.ekart.entity.Image;
import com.shoppers.ekart.enums.ImageType;

public interface ImageRepository extends MongoRepository<Image, String>{

	@Query("{'imageType': ?0, 'storeId': ?1}")
    Optional<Image> findImageByImageTypeAndStoreId(ImageType imageType, int storeId);
}
