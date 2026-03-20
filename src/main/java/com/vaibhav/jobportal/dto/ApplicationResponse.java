package com.vaibhav.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ApplicationResponse {

	private Long id;
	private Long userId;
	private String applicantName;
	private String applicantEmail;
	private String applicantSkills;
	private Long jobId;
	private String jobTitle;
	private String company;
	private String location;
	private LocalDate appliedDate;
}
