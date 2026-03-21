package com.vaibhav.jobportal.controller;

import com.vaibhav.jobportal.dto.ApiResponse;
import com.vaibhav.jobportal.dto.AuthRequest;
import com.vaibhav.jobportal.dto.AuthResponse;
import com.vaibhav.jobportal.dto.RegisterRequest;
import com.vaibhav.jobportal.dto.VerifyOtpRequest;
import com.vaibhav.jobportal.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
		authService.requestRegistrationOtp(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ApiResponse<>(true, "OTP sent to email and phone.", null));
	}

	@PostMapping("/register/verify")
	public ResponseEntity<ApiResponse<AuthResponse>> verify(@Valid @RequestBody VerifyOtpRequest request) {
		AuthResponse response = authService.verifyRegistrationOtp(request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ApiResponse<>(true, response.getMessage(), response));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
		AuthResponse response = authService.login(request);
		return ResponseEntity.ok(new ApiResponse<>(true, response.getMessage(), response));
	}
}
