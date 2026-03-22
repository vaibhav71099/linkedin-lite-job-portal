package com.vaibhav.jobportal.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConversationResponse {

	private Long id;
	private UserResponse otherParticipant;
	private String lastMessagePreview;
	private Instant updatedAt;
	private long unreadCount;
}
