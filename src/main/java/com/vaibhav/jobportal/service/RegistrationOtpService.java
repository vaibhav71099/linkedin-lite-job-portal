package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.dto.RegisterRequest;
import com.vaibhav.jobportal.dto.VerifyOtpRequest;
import com.vaibhav.jobportal.entity.PendingRegistration;
import com.vaibhav.jobportal.entity.Role;
import com.vaibhav.jobportal.entity.User;
import com.vaibhav.jobportal.exception.InvalidRoleException;
import com.vaibhav.jobportal.exception.OtpDeliveryException;
import com.vaibhav.jobportal.exception.OtpExpiredException;
import com.vaibhav.jobportal.exception.OtpInvalidException;
import com.vaibhav.jobportal.exception.PendingRegistrationNotFoundException;
import com.vaibhav.jobportal.exception.UserAlreadyExistsException;
import com.vaibhav.jobportal.repository.PendingRegistrationRepository;
import com.vaibhav.jobportal.repository.UserRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationOtpService {

	private static final int OTP_LENGTH = 6;
	private static final String SMS_OTP_DISABLED_SENTINEL = "SMS_OTP_DISABLED";

	private final PendingRegistrationRepository pendingRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailOtpService emailOtpService;
	private final int otpExpiryMinutes;
	private final SecureRandom secureRandom = new SecureRandom();

	public RegistrationOtpService(
		PendingRegistrationRepository pendingRepository,
		UserRepository userRepository,
		PasswordEncoder passwordEncoder,
		EmailOtpService emailOtpService,
		@Value("${app.otp.expiry-minutes:10}") int otpExpiryMinutes
	) {
		this.pendingRepository = pendingRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailOtpService = emailOtpService;
		this.otpExpiryMinutes = otpExpiryMinutes;
	}

	public boolean sendRegistrationOtps(RegisterRequest request) {
		String email = normalizeEmail(request.getEmail());

		if (userRepository.existsByEmail(email)) {
			throw new UserAlreadyExistsException("User with this email already exists.");
		}
		if (request.getRole() == Role.ADMIN) {
			throw new InvalidRoleException("Admin registration is not allowed.");
		}

		PendingRegistration pending = pendingRepository.findByEmail(email).orElse(null);
		if (pending == null) {
			pending = new PendingRegistration();
			if (pending.getCreatedAt() == null) {
				pending.setCreatedAt(Instant.now());
			}
		}

		String emailOtp = generateOtp();
		Instant expiresAt = Instant.now().plus(otpExpiryMinutes, ChronoUnit.MINUTES);
		boolean phoneOtpRequired = false;

		pending.setName(request.getName().trim());
		pending.setEmail(email);
		pending.setPhone(null);
		pending.setRole(request.getRole());
		pending.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		pending.setEmailOtpHash(passwordEncoder.encode(emailOtp));
		pending.setEmailOtpExpiresAt(expiresAt);
		pending.setPhoneOtpExpiresAt(expiresAt);
		pending.setEmailVerified(Boolean.FALSE);
		pending.setPhoneVerified(Boolean.FALSE);
		pending.setUpdatedAt(Instant.now());
		pending.setPhoneOtpHash(passwordEncoder.encode(SMS_OTP_DISABLED_SENTINEL));

		pendingRepository.save(pending);

		try {
			emailOtpService.sendOtp(email, emailOtp);
		} catch (OtpDeliveryException ex) {
			pendingRepository.delete(pending);
			throw ex;
		}
		return phoneOtpRequired;
	}

	public User verifyRegistrationOtps(VerifyOtpRequest request) {
		String email = normalizeEmail(request.getEmail());

		PendingRegistration pending = pendingRepository.findByEmail(email)
			.orElseThrow(() -> new PendingRegistrationNotFoundException("No pending registration found."));

		Instant now = Instant.now();
		if (pending.getEmailOtpExpiresAt().isBefore(now)) {
			throw new OtpExpiredException("OTP has expired. Please request a new one.");
		}

		if (!passwordEncoder.matches(request.getEmailOtp(), pending.getEmailOtpHash())) {
			throw new OtpInvalidException("Invalid email OTP.");
		}

		if (userRepository.existsByEmail(email)) {
			throw new UserAlreadyExistsException("User with this email already exists.");
		}

		User user = new User();
		user.setName(pending.getName());
		user.setBio("");
		user.setSkills("");
		user.setEmail(pending.getEmail());
		user.setPhone(null);
		user.setRole(pending.getRole());
		user.setPassword(pending.getPasswordHash());
		user.setEmailVerified(Boolean.TRUE);
		user.setPhoneVerified(Boolean.FALSE);
		User savedUser = userRepository.save(user);

		pendingRepository.delete(pending);
		return savedUser;
	}

	private String generateOtp() {
		int max = (int) Math.pow(10, OTP_LENGTH);
		int code = secureRandom.nextInt(max);
		return String.format(Locale.US, "%0" + OTP_LENGTH + "d", code);
	}

	private String normalizeEmail(String email) {
		return email == null ? "" : email.trim().toLowerCase(Locale.US);
	}

}
