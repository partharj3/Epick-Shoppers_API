package com.shoppers.ekart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppers.ekart.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Integer>{
//	boolean existsByAddress();
//	boolean existsByStoreNameAndAddress_StreetAddress(String storeName);
}
