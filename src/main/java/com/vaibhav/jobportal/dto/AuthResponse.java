package com.vaibhav.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

	private String token;
	private String message;
	private UserResponse user;
}
