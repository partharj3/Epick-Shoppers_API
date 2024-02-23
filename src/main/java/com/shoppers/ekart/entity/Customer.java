package com.shoppers.ekart.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "Customers")
@Entity
@Getter
@Setter
public class Customer extends User{ 
	
	@OneToMany(mappedBy = "customer")
	private List<Address> addresses;
}
