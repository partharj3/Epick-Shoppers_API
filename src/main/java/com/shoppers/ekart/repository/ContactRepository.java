package com.shoppers.ekart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoppers.ekart.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer>{

}
