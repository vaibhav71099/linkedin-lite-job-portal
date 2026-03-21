package com.vaibhav.jobportal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {

	@NotBlank(message = "Email is required.")
	@Email(message = "Email must be valid.")
	private String email;

	@NotBlank(message = "Phone number is required.")
	@Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid.")
	private String phone;

	@NotBlank(message = "Email OTP is required.")
	@Pattern(regexp = "^\\d{6}$", message = "Email OTP must be 6 digits.")
	private String emailOtp;

	@NotBlank(message = "Phone OTP is required.")
	@Pattern(regexp = "^\\d{6}$", message = "Phone OTP must be 6 digits.")
	private String phoneOtp;
}
