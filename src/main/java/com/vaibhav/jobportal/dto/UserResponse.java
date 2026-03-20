package com.vaibhav.jobportal.dto;

import com.vaibhav.jobportal.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

	private Long id;
	private String name;
	private String bio;
	private String skills;
	private String email;
	private Role role;
	private Boolean emailVerified;
}
