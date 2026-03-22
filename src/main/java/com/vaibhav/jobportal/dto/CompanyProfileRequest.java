package com.vaibhav.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyProfileRequest {

	@NotBlank(message = "Company name is required.")
	@Size(max = 160, message = "Company name must be at most 160 characters.")
	private String name;

	@Size(max = 180, message = "Slogan must be at most 180 characters.")
	private String slogan;

	@Size(max = 120, message = "Industry must be at most 120 characters.")
	private String industry;

	@Size(max = 1000, message = "About must be at most 1000 characters.")
	private String about;

	@Size(max = 120, message = "Company size must be at most 120 characters.")
	private String size;

	@Size(max = 120, message = "Headquarters must be at most 120 characters.")
	private String headquarters;

	@Size(max = 255, message = "Website must be at most 255 characters.")
	private String website;

	@Size(max = 1000, message = "Cover image URL must be at most 1000 characters.")
	private String coverImageUrl;
}
