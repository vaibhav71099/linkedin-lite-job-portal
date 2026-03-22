package com.vaibhav.jobportal.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchResponse {

	private List<UserResponse> people;
	private List<JobResponse> jobs;
	private List<CompanyProfileResponse> companies;
}
