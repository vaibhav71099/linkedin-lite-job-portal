package com.vaibhav.jobportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {

	@NotBlank(message = "Email is required.")
	@Email(message = "Email must be valid.")
	private String email;

	@NotBlank(message = "Email OTP is required.")
	@jakarta.validation.constraints.Pattern(regexp = "^\\d{6}$", message = "Email OTP must be 6 digits.")
	private String emailOtp;
}
