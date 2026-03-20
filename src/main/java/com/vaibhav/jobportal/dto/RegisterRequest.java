package com.vaibhav.jobportal.dto;

import com.vaibhav.jobportal.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

	@NotBlank(message = "Name is required.")
	@Size(max = 100, message = "Name must be at most 100 characters.")
	private String name;

	@NotBlank(message = "Email is required.")
	@Email(message = "Email must be valid.")
	private String email;

	@NotBlank(message = "Password is required.")
	@Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters.")
	private String password;

	@NotNull(message = "Role is required.")
	private Role role;
}
