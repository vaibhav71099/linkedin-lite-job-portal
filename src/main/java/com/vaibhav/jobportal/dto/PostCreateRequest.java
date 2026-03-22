package com.vaibhav.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequest {

	@NotBlank(message = "Post content is required.")
	@Size(max = 2200, message = "Post content must be at most 2200 characters.")
	private String content;

	@Size(max = 1000, message = "Image URL must be at most 1000 characters.")
	private String imageUrl;
}
