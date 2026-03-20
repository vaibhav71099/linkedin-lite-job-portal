package com.vaibhav.jobportal.controller;

import com.vaibhav.jobportal.dto.ApiResponse;
import com.vaibhav.jobportal.dto.UserProfileUpdateRequest;
import com.vaibhav.jobportal.dto.UserResponse;
import com.vaibhav.jobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
		UserResponse response = userService.getCurrentUserProfile(authentication.getName());
		return ResponseEntity.ok(new ApiResponse<>(true, "User profile fetched successfully.", response));
	}

	@PutMapping("/me")
	public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
		Authentication authentication,
		@Valid @RequestBody UserProfileUpdateRequest request
	) {
		UserResponse response = userService.updateCurrentUserProfile(authentication.getName(), request);
		return ResponseEntity.ok(new ApiResponse<>(true, "User profile updated successfully.", response));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
	public ResponseEntity<ApiResponse<java.util.List<UserResponse>>> getAllUsers() {
		return ResponseEntity.ok(
			new ApiResponse<>(true, "Users fetched successfully.", userService.getAllUsers())
		);
	}
}
