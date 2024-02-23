package com.shoppers.ekart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.shoppers.ekart.entity.Seller;

public interface SellerRepository extends JpaRepository<Seller, Integer>{
}
