package com.vaibhav.jobportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {

	@NotBlank(message = "Message is required.")
	@Size(max = 2000, message = "Message must be at most 2000 characters.")
	private String content;
}
