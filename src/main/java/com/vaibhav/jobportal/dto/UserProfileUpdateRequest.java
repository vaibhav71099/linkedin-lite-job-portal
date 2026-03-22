package com.vaibhav.jobportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateRequest {

	@NotBlank(message = "Name is required.")
	@Size(max = 100, message = "Name must be at most 100 characters.")
	private String name;

	@NotBlank(message = "Email is required.")
	@Email(message = "Email must be valid.")
	private String email;

	@Size(max = 160, message = "Headline must be at most 160 characters.")
	private String headline;

	@Size(max = 160, message = "Location must be at most 160 characters.")
	private String location;

	@Size(max = 160, message = "Current company must be at most 160 characters.")
	private String currentCompany;

	@Size(max = 160, message = "Education must be at most 160 characters.")
	private String education;

	@Size(max = 1000, message = "Bio must be at most 1000 characters.")
	private String bio;

	@Size(max = 1000, message = "Skills must be at most 1000 characters.")
	private String skills;
}
