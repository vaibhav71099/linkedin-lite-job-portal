package com.vaibhav.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRequest {

	@NotBlank(message = "Title is required.")
	@Size(max = 150, message = "Title must be at most 150 characters.")
	private String title;

	@NotBlank(message = "Description is required.")
	@Size(max = 2000, message = "Description must be at most 2000 characters.")
	private String description;

	@NotBlank(message = "Company is required.")
	@Size(max = 150, message = "Company must be at most 150 characters.")
	private String company;

	@NotBlank(message = "Location is required.")
	@Size(max = 150, message = "Location must be at most 150 characters.")
	private String location;
}
