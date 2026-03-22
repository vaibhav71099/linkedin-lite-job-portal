package com.vaibhav.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyProfileResponse {

	private Long id;
	private String name;
	private String slogan;
	private String industry;
	private String about;
	private String size;
	private String headquarters;
	private String website;
	private String coverImageUrl;
	private Long ownerId;
	private String ownerName;
	private long activeJobs;
}
