package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.dto.RegisterRequest;
import com.vaibhav.jobportal.dto.UserProfileUpdateRequest;
import com.vaibhav.jobportal.dto.UserResponse;
import com.vaibhav.jobportal.entity.Role;
import com.vaibhav.jobportal.entity.User;
import com.vaibhav.jobportal.exception.InvalidRoleException;
import com.vaibhav.jobportal.exception.UserAlreadyExistsException;
import com.vaibhav.jobportal.exception.UserNotFoundException;
import com.vaibhav.jobportal.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public User registerUser(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new UserAlreadyExistsException("User with this email already exists.");
		}

		User user = new User();
		user.setName(request.getName());
		user.setBio("");
		user.setSkills("");
		user.setEmail(request.getEmail());
		user.setPhone(null);
		if (request.getRole() == Role.ADMIN) {
			throw new InvalidRoleException("Admin registration is not allowed.");
		}
		user.setRole(request.getRole());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEmailVerified(Boolean.FALSE);
		user.setPhoneVerified(Boolean.FALSE);
		return userRepository.save(user);
	}

	public List<UserResponse> getAllUsers() {
		return userRepository.findAll()
			.stream()
			.map(this::toUserResponse)
			.toList();
	}

	public User getUserById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
	}

	public UserResponse getCurrentUserProfile(String email) {
		return toUserResponse(getUserByEmail(email));
	}

	public UserResponse updateCurrentUserProfile(String email, UserProfileUpdateRequest request) {
		User user = getUserByEmail(email);

		if (userRepository.existsByEmailAndIdNot(request.getEmail(), user.getId())) {
			throw new UserAlreadyExistsException("User with this email already exists.");
		}

		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setBio(request.getBio() == null ? "" : request.getBio());
		user.setSkills(request.getSkills() == null ? "" : request.getSkills());

		return toUserResponse(userRepository.save(user));
	}

	public UserResponse toUserResponse(User user) {
		return new UserResponse(
			user.getId(),
			user.getName(),
			user.getBio(),
			user.getSkills(),
			user.getEmail(),
			user.getPhone(),
			user.getRole(),
			user.getEmailVerified(),
			user.getPhoneVerified() == null ? Boolean.FALSE : user.getPhoneVerified()
		);
	}
}
