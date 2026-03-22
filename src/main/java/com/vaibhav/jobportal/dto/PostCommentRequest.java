package com.vaibhav.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCommentRequest {

	@NotBlank(message = "Comment is required.")
	@Size(max = 1000, message = "Comment must be at most 1000 characters.")
	private String content;
}
