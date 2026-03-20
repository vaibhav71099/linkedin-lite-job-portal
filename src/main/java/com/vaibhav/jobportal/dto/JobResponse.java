package com.vaibhav.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobResponse {

	private Long id;
	private String title;
	private String description;
	private String company;
	private String location;
	private Long recruiterId;
	private String recruiterName;
}
