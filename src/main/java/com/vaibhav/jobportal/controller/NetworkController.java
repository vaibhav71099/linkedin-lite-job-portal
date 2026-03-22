package com.vaibhav.jobportal.controller;

import com.vaibhav.jobportal.dto.ApiResponse;
import com.vaibhav.jobportal.dto.ConnectionRequestResponse;
import com.vaibhav.jobportal.dto.UserResponse;
import com.vaibhav.jobportal.service.NetworkService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/network")
public class NetworkController {

	private final NetworkService networkService;

	public NetworkController(NetworkService networkService) {
		this.networkService = networkService;
	}

	@GetMapping("/discover")
	public ResponseEntity<ApiResponse<List<UserResponse>>> discover(Authentication authentication) {
		return ResponseEntity.ok(new ApiResponse<>(
			true,
			"People fetched successfully.",
			networkService.getDiscoverPeople(authentication.getName())
		));
	}

	@GetMapping("/invitations")
	public ResponseEntity<ApiResponse<List<ConnectionRequestResponse>>> invitations(Authentication authentication) {
		return ResponseEntity.ok(new ApiResponse<>(
			true,
			"Invitations fetched successfully.",
			networkService.getPendingInvitations(authentication.getName())
		));
	}

	@GetMapping("/connections")
	public ResponseEntity<ApiResponse<List<UserResponse>>> connections(Authentication authentication) {
		return ResponseEntity.ok(new ApiResponse<>(
			true,
			"Connections fetched successfully.",
			networkService.getConnections(authentication.getName())
		));
	}

	@PostMapping("/requests/{userId}")
	public ResponseEntity<ApiResponse<ConnectionRequestResponse>> sendRequest(
		Authentication authentication,
		@PathVariable Long userId
	) {
		return ResponseEntity.ok(new ApiResponse<>(
			true,
			"Invitation sent successfully.",
			networkService.sendConnectionRequest(authentication.getName(), userId)
		));
	}

	@PostMapping("/requests/{requestId}/accept")
	public ResponseEntity<ApiResponse<ConnectionRequestResponse>> acceptRequest(
		Authentication authentication,
		@PathVariable Long requestId
	) {
		return ResponseEntity.ok(new ApiResponse<>(
			true,
			"Invitation accepted.",
			networkService.acceptConnectionRequest(authentication.getName(), requestId)
		));
	}

	@PostMapping("/requests/{requestId}/ignore")
	public ResponseEntity<ApiResponse<ConnectionRequestResponse>> ignoreRequest(
		Authentication authentication,
		@PathVariable Long requestId
	) {
		return ResponseEntity.ok(new ApiResponse<>(
			true,
			"Invitation ignored.",
			networkService.ignoreConnectionRequest(authentication.getName(), requestId)
		));
	}
}
