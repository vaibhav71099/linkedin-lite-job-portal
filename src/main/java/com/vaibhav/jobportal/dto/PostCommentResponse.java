package com.vaibhav.jobportal.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCommentResponse {

	private Long id;
	private UserResponse author;
	private String content;
	private Instant createdAt;
}
