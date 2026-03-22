package com.vaibhav.jobportal.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageResponse {

	private Long id;
	private Long conversationId;
	private UserResponse sender;
	private UserResponse receiver;
	private String content;
	private boolean read;
	private Instant createdAt;
}
