package com.shoppers.ekart.requestdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
	
	@NotBlank(message = "Student Email field Should not be BLANK")
	@Email(regexp = "[a-z0-9+_.-]+@[g][m][a][i][l]+.[c][o][m]", 
		   message = "invalid email--Should be in the extension of '@gmail.com' ")
	private String email;
	
	@NotEmpty(message = "Password is required")
	@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", 
	         message = "Password must contain at least one upper case, one lower case, one number, one special character")
	private String password;
	
	@NotEmpty(message = "Please provide your Role")
	@Pattern(regexp="^[A-Z]+$", message="Role should be in Upper Case")	
	private String userRole;	
}
