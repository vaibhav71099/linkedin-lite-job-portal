package com.vaibhav.jobportal.controller;

import com.vaibhav.jobportal.dto.ApiResponse;
import com.vaibhav.jobportal.dto.JobRequest;
import com.vaibhav.jobportal.dto.JobResponse;
import com.vaibhav.jobportal.dto.PagedResponse;
import com.vaibhav.jobportal.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {

	private final JobService jobService;

	public JobController(JobService jobService) {
		this.jobService = jobService;
	}

	@PostMapping
	@PreAuthorize("hasRole('RECRUITER')")
	public ResponseEntity<ApiResponse<JobResponse>> createJob(
		@Valid @RequestBody JobRequest request,
		Authentication authentication
	) {
		JobResponse response = jobService.createJob(request, authentication.getName());
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ApiResponse<>(true, "Job created successfully.", response));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<PagedResponse<JobResponse>>> getAllJobs(
		@RequestParam(defaultValue = "") String title,
		@RequestParam(defaultValue = "") String location,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		return ResponseEntity.ok(
			new ApiResponse<>(true, "Jobs fetched successfully.", jobService.getAllJobs(title, location, page, size))
		);
	}

	@GetMapping("/mine")
	@PreAuthorize("hasRole('RECRUITER')")
	public ResponseEntity<ApiResponse<java.util.List<JobResponse>>> getMyJobs(Authentication authentication) {
		return ResponseEntity.ok(
			new ApiResponse<>(true, "Recruiter jobs fetched successfully.",
				jobService.getRecruiterJobs(authentication.getName()))
		);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('RECRUITER')")
	public ResponseEntity<ApiResponse<JobResponse>> updateJob(
		@PathVariable Long id,
		@Valid @RequestBody JobRequest request,
		Authentication authentication
	) {
		return ResponseEntity.ok(
			new ApiResponse<>(true, "Job updated successfully.", jobService.updateJob(id, request, authentication.getName()))
		);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('RECRUITER')")
	public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id, Authentication authentication) {
		jobService.deleteJob(id, authentication.getName());
		return ResponseEntity.ok(new ApiResponse<>(true, "Job deleted successfully.", null));
	}
}
