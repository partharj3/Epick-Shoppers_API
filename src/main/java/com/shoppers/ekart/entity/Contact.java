package com.shoppers.ekart.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shoppers.ekart.enums.Priority;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int contactId;
	private long contactNumber;
	private Priority priority;
	
	@JsonIgnore // It stop at contact, it wont provide the Address information because of bi-directional mapping
	@ManyToOne
	private Address address;
	
}
