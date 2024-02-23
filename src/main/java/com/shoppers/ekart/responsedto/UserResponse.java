package com.shoppers.ekart.responsedto;

import com.shoppers.ekart.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
	private int userId;
	private String username;
	private String email;
	private UserRole userRole;
}
