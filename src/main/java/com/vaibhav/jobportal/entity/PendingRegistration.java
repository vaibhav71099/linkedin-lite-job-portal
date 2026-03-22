package com.vaibhav.jobportal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pending_registrations")
@Getter
@Setter
@NoArgsConstructor
public class PendingRegistration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String name;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(unique = true)
	private String phone;

	@Column(nullable = false, length = 255)
	private String passwordHash;

	@Column(nullable = false)
	@Convert(converter = RoleConverter.class)
	private Role role;

	@Column(nullable = false, length = 255)
	private String emailOtpHash;

	@Column(nullable = false, length = 255)
	private String phoneOtpHash;

	@Column(nullable = false)
	private Instant emailOtpExpiresAt;

	@Column(nullable = false)
	private Instant phoneOtpExpiresAt;

	@Column(nullable = false)
	private Boolean emailVerified;

	@Column(nullable = false)
	private Boolean phoneVerified;

	@Column(nullable = false)
	private Instant createdAt;

	@Column(nullable = false)
	private Instant updatedAt;
}
