package com.vaibhav.jobportal.dto;

import com.vaibhav.jobportal.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

	private Long id;
	private String name;
	private String headline;
	private String location;
	private String currentCompany;
	private String education;
	private String bio;
	private String skills;
	private String email;
	private String phone;
	private Role role;
	private Boolean emailVerified;
	private Boolean phoneVerified;
}
