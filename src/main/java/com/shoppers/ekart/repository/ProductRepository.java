package com.shoppers.ekart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.shoppers.ekart.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{

}
