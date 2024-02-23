package com.shoppers.ekart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.shoppers.ekart.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer>{

}
