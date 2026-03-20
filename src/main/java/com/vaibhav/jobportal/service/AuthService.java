package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.dto.AuthRequest;
import com.vaibhav.jobportal.dto.AuthResponse;
import com.vaibhav.jobportal.dto.RegisterRequest;
import com.vaibhav.jobportal.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final UserService userService;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final ApplicationUserDetailsService userDetailsService;

	public AuthService(UserService userService,
					  JwtService jwtService,
					  AuthenticationManager authenticationManager,
						  ApplicationUserDetailsService userDetailsService) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
	}

	public AuthResponse register(RegisterRequest request) {
		var savedUser = userService.registerUser(request);
		UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
		String token = jwtService.generateToken(userDetails);
		return new AuthResponse(token, "User registered successfully.", userService.toUserResponse(savedUser));
	}

	public AuthResponse login(AuthRequest request) {
		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
		);

		UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
		String token = jwtService.generateToken(userDetails);
		return new AuthResponse(
			token,
			"Login successful.",
			userService.getCurrentUserProfile(request.getEmail())
		);
	}
}
