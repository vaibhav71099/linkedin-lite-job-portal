package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.dto.RegisterRequest;
import com.vaibhav.jobportal.dto.VerifyOtpRequest;
import com.vaibhav.jobportal.entity.PendingRegistration;
import com.vaibhav.jobportal.entity.Role;
import com.vaibhav.jobportal.entity.User;
import com.vaibhav.jobportal.exception.InvalidRoleException;
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
	private final SmsOtpService smsOtpService;
	private final int otpExpiryMinutes;
	private final SecureRandom secureRandom = new SecureRandom();

	public RegistrationOtpService(
		PendingRegistrationRepository pendingRepository,
		UserRepository userRepository,
		PasswordEncoder passwordEncoder,
		EmailOtpService emailOtpService,
		SmsOtpService smsOtpService,
		@Value("${app.otp.expiry-minutes:10}") int otpExpiryMinutes
	) {
		this.pendingRepository = pendingRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.emailOtpService = emailOtpService;
		this.smsOtpService = smsOtpService;
		this.otpExpiryMinutes = otpExpiryMinutes;
	}

	public boolean sendRegistrationOtps(RegisterRequest request) {
		String email = normalizeEmail(request.getEmail());
		String phone = normalizePhone(request.getPhone());

		if (userRepository.existsByEmail(email)) {
			throw new UserAlreadyExistsException("User with this email already exists.");
		}
		if (userRepository.existsByPhone(phone)) {
			throw new UserAlreadyExistsException("User with this phone number already exists.");
		}
		if (request.getRole() == Role.ADMIN) {
			throw new InvalidRoleException("Admin registration is not allowed.");
		}

		PendingRegistration pending = pendingRepository.findByEmail(email).orElse(null);
		if (pending != null && !pending.getPhone().equals(phone)) {
			throw new UserAlreadyExistsException("A pending registration already exists for this email.");
		}
		PendingRegistration pendingByPhone = pendingRepository.findByPhone(phone).orElse(null);
		if (pendingByPhone != null && (pending == null || !pendingByPhone.getEmail().equals(email))) {
			throw new UserAlreadyExistsException("A pending registration already exists for this phone number.");
		}
		if (pending == null) {
			pending = pendingByPhone != null ? pendingByPhone : new PendingRegistration();
			if (pending.getCreatedAt() == null) {
				pending.setCreatedAt(Instant.now());
			}
		}

		String emailOtp = generateOtp();
		Instant expiresAt = Instant.now().plus(otpExpiryMinutes, ChronoUnit.MINUTES);
		boolean phoneOtpRequired = smsOtpService.isConfigured();

		pending.setName(request.getName().trim());
		pending.setEmail(email);
		pending.setPhone(phone);
		pending.setRole(request.getRole());
		pending.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		pending.setEmailOtpHash(passwordEncoder.encode(emailOtp));
		pending.setEmailOtpExpiresAt(expiresAt);
		pending.setPhoneOtpExpiresAt(expiresAt);
		pending.setEmailVerified(Boolean.FALSE);
		pending.setPhoneVerified(Boolean.FALSE);
		pending.setUpdatedAt(Instant.now());
		if (phoneOtpRequired) {
			String phoneOtp = generateOtp();
			pending.setPhoneOtpHash(passwordEncoder.encode(phoneOtp));
			smsOtpService.sendOtp(phone, phoneOtp);
		} else {
			pending.setPhoneOtpHash(passwordEncoder.encode(SMS_OTP_DISABLED_SENTINEL));
		}

		pendingRepository.save(pending);

		emailOtpService.sendOtp(email, emailOtp);
		return phoneOtpRequired;
	}

	public User verifyRegistrationOtps(VerifyOtpRequest request) {
		String email = normalizeEmail(request.getEmail());
		String phone = normalizePhone(request.getPhone());

		PendingRegistration pending = pendingRepository.findByEmail(email)
			.orElseThrow(() -> new PendingRegistrationNotFoundException("No pending registration found."));

		if (!pending.getPhone().equals(phone)) {
			throw new OtpInvalidException("Phone number does not match the pending registration.");
		}

		Instant now = Instant.now();
		if (pending.getEmailOtpExpiresAt().isBefore(now)) {
			throw new OtpExpiredException("OTP has expired. Please request a new one.");
		}

		if (!passwordEncoder.matches(request.getEmailOtp(), pending.getEmailOtpHash())) {
			throw new OtpInvalidException("Invalid email OTP.");
		}
		boolean phoneOtpRequired = isPhoneOtpRequired(pending);
		if (phoneOtpRequired && pending.getPhoneOtpExpiresAt().isBefore(now)) {
			throw new OtpExpiredException("OTP has expired. Please request a new one.");
		}
		if (phoneOtpRequired && !passwordEncoder.matches(normalizeOtp(request.getPhoneOtp()), pending.getPhoneOtpHash())) {
			throw new OtpInvalidException("Invalid phone OTP.");
		}

		if (userRepository.existsByEmail(email)) {
			throw new UserAlreadyExistsException("User with this email already exists.");
		}
		if (userRepository.existsByPhone(phone)) {
			throw new UserAlreadyExistsException("User with this phone number already exists.");
		}

		User user = new User();
		user.setName(pending.getName());
		user.setBio("");
		user.setSkills("");
		user.setEmail(pending.getEmail());
		user.setPhone(pending.getPhone());
		user.setRole(pending.getRole());
		user.setPassword(pending.getPasswordHash());
		user.setEmailVerified(Boolean.TRUE);
		user.setPhoneVerified(phoneOtpRequired);
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

	private String normalizePhone(String phone) {
		return phone == null ? "" : phone.trim();
	}

	private String normalizeOtp(String otp) {
		return otp == null ? "" : otp.trim();
	}

	private boolean isPhoneOtpRequired(PendingRegistration pending) {
		return !passwordEncoder.matches(SMS_OTP_DISABLED_SENTINEL, pending.getPhoneOtpHash());
	}
}
