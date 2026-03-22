package com.vaibhav.jobportal.controller;

import com.vaibhav.jobportal.dto.ApiResponse;
import com.vaibhav.jobportal.dto.ConversationResponse;
import com.vaibhav.jobportal.dto.MessageRequest;
import com.vaibhav.jobportal.dto.MessageResponse;
import com.vaibhav.jobportal.service.MessagingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessagingController {

	private final MessagingService messagingService;

	public MessagingController(MessagingService messagingService) {
		this.messagingService = messagingService;
	}

	@GetMapping("/conversations")
	public ResponseEntity<ApiResponse<List<ConversationResponse>>> getConversations(Authentication authentication) {
		return ResponseEntity.ok(new ApiResponse<>(
			true,
			"Conversations fetched successfully.",
			messagingService.getConversations(authentication.getName())
		));
	}

	@GetMapping("/conversations/{conversationId}")
	public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
		Authentication authentication,
		@PathVariable Long conversationId
	) {
		return ResponseEntity.ok(new ApiResponse<>(
			true,
			"Messages fetched successfully.",
			messagingService.getMessages(authentication.getName(), conversationId)
		));
	}

	@PostMapping("/users/{userId}")
	public ResponseEntity<ApiResponse<MessageResponse>> sendMessage(
		Authentication authentication,
		@PathVariable Long userId,
		@Valid @RequestBody MessageRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
			true,
			"Message sent successfully.",
			messagingService.sendMessage(authentication.getName(), userId, request)
		));
	}
}
