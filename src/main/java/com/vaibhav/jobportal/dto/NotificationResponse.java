package com.vaibhav.jobportal.dto;

import com.vaibhav.jobportal.entity.NotificationType;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationResponse {

	private Long id;
	private NotificationType type;
	private String title;
	private String body;
	private String targetPath;
	private boolean read;
	private Instant createdAt;
	private UserResponse actor;
}
