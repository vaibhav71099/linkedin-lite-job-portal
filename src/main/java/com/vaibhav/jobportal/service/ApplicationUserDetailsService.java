package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.entity.User;
import com.vaibhav.jobportal.exception.UserNotFoundException;
import com.vaibhav.jobportal.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public ApplicationUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found with this email."));

		return new org.springframework.security.core.userdetails.User(
			user.getEmail(),
			user.getPassword(),
			List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
		);
	}
}
