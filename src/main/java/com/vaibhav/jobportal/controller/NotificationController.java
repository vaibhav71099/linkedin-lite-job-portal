package com.vaibhav.jobportal.controller;

import com.vaibhav.jobportal.dto.ApiResponse;
import com.vaibhav.jobportal.dto.NotificationResponse;
import com.vaibhav.jobportal.service.NotificationService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	private final NotificationService notificationService;

	public NotificationController(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(Authentication authentication) {
		return ResponseEntity.ok(new ApiResponse<>(
			true,
			"Notifications fetched successfully.",
			notificationService.getNotifications(authentication.getName())
		));
	}

	@PostMapping("/{notificationId}/read")
	public ResponseEntity<ApiResponse<Void>> markAsRead(
		Authentication authentication,
		@PathVariable Long notificationId
	) {
		notificationService.markAsRead(authentication.getName(), notificationId);
		return ResponseEntity.ok(new ApiResponse<>(true, "Notification marked as read.", null));
	}
}
