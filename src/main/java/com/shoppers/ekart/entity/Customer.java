package com.shoppers.ekart.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "Customers")
@Entity
public class Customer extends User{ }
