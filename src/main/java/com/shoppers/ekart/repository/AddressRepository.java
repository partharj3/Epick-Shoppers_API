package com.shoppers.ekart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppers.ekart.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer>{

}
