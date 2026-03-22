package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.dto.AuthRequest;
import com.vaibhav.jobportal.dto.AuthResponse;
import com.vaibhav.jobportal.dto.RegisterRequest;
import com.vaibhav.jobportal.dto.RegistrationOtpResponse;
import com.vaibhav.jobportal.dto.VerifyOtpRequest;
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
	private final RegistrationOtpService registrationOtpService;

	public AuthService(UserService userService,
					  JwtService jwtService,
					  AuthenticationManager authenticationManager,
						  ApplicationUserDetailsService userDetailsService,
					  RegistrationOtpService registrationOtpService) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.registrationOtpService = registrationOtpService;
	}

	public RegistrationOtpResponse requestRegistrationOtp(RegisterRequest request) {
		boolean phoneOtpRequired = registrationOtpService.sendRegistrationOtps(request);
		return new RegistrationOtpResponse(phoneOtpRequired);
	}

	public AuthResponse verifyRegistrationOtp(VerifyOtpRequest request) {
		var savedUser = registrationOtpService.verifyRegistrationOtps(request);
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
