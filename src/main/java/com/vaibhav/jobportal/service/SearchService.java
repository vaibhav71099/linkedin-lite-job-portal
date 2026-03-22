package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.dto.JobResponse;
import com.vaibhav.jobportal.dto.SearchResponse;
import com.vaibhav.jobportal.dto.UserResponse;
import com.vaibhav.jobportal.repository.JobRepository;
import com.vaibhav.jobportal.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

	private final UserRepository userRepository;
	private final JobRepository jobRepository;
	private final UserService userService;
	private final JobService jobService;
	private final CompanyService companyService;

	public SearchService(
		UserRepository userRepository,
		JobRepository jobRepository,
		UserService userService,
		JobService jobService,
		CompanyService companyService
	) {
		this.userRepository = userRepository;
		this.jobRepository = jobRepository;
		this.userService = userService;
		this.jobService = jobService;
		this.companyService = companyService;
	}

	public SearchResponse search(String query) {
		String normalized = query == null ? "" : query.trim().toLowerCase();

		List<UserResponse> people = userRepository.findAll().stream()
			.filter(user -> contains(user.getName(), normalized)
				|| contains(user.getHeadline(), normalized)
				|| contains(user.getCurrentCompany(), normalized)
				|| contains(user.getLocation(), normalized))
			.limit(8)
			.map(userService::toUserResponse)
			.toList();

		List<JobResponse> jobs = jobRepository.findAll().stream()
			.filter(job -> contains(job.getTitle(), normalized)
				|| contains(job.getCompany(), normalized)
				|| contains(job.getLocation(), normalized)
				|| contains(job.getDescription(), normalized))
			.limit(8)
			.map(jobService::toJobResponse)
			.toList();

		return new SearchResponse(people, jobs, companyService.getCompanies(query).stream().limit(8).toList());
	}

	private boolean contains(String value, String query) {
		if (query.isBlank()) {
			return true;
		}
		return value != null && value.toLowerCase().contains(query);
	}
}
