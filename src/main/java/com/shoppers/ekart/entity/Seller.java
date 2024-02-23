package com.shoppers.ekart.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "Sellers")
@Entity
@Getter
@Setter
public class Seller extends User{
	@OneToOne
	private Store store;
}
