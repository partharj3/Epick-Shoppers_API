package com.shoppers.ekart.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.shoppers.ekart.entity.Image;

public interface ImageRepository extends MongoRepository<Image, String>{

}
