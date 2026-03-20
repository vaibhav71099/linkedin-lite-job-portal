package com.vaibhav.jobportal.controller;

import com.vaibhav.jobportal.dto.ApiResponse;
import com.vaibhav.jobportal.dto.ApplicationResponse;
import com.vaibhav.jobportal.dto.ApplicationRequest;
import com.vaibhav.jobportal.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApplicationController {

	private final ApplicationService applicationService;

	public ApplicationController(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	@PostMapping("/api/applications")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse<ApplicationResponse>> applyForJob(
		@Valid @RequestBody ApplicationRequest request,
		Authentication authentication
	) {
		ApplicationResponse response = applicationService.applyForJob(authentication.getName(), request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ApiResponse<>(true, "Job application submitted successfully.", response));
	}

	@PostMapping("/apply")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse<ApplicationResponse>> applyForJobAtRoot(
		@Valid @RequestBody ApplicationRequest request,
		Authentication authentication
	) {
		ApplicationResponse response = applicationService.applyForJob(authentication.getName(), request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(new ApiResponse<>(true, "Job application submitted successfully.", response));
	}

	@GetMapping("/api/applications/my")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getMyApplications(Authentication authentication) {
		return ResponseEntity.ok(
			new ApiResponse<>(true, "User applications fetched successfully.",
				applicationService.getMyApplications(authentication.getName()))
		);
	}

	@GetMapping("/applications")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getMyApplicationsAtRoot(Authentication authentication) {
		return ResponseEntity.ok(
			new ApiResponse<>(true, "User applications fetched successfully.",
				applicationService.getMyApplications(authentication.getName()))
		);
	}

	@GetMapping("/api/applications")
	@PreAuthorize("hasRole('RECRUITER')")
	public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getRecruiterApplications(Authentication authentication) {
		return ResponseEntity.ok(
			new ApiResponse<>(true, "Applicants fetched successfully.",
				applicationService.getRecruiterApplicants(authentication.getName()))
		);
	}

	@GetMapping("/applicants")
	@PreAuthorize("hasRole('RECRUITER')")
	public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getRecruiterApplicants(Authentication authentication) {
		return ResponseEntity.ok(
			new ApiResponse<>(true, "Applicants fetched successfully.",
				applicationService.getRecruiterApplicants(authentication.getName()))
		);
	}

	@GetMapping("/api/applications/job/{jobId}")
	@PreAuthorize("hasRole('RECRUITER')")
	public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplicantsForJob(
		@PathVariable Long jobId,
		Authentication authentication
	) {
		return ResponseEntity.ok(
			new ApiResponse<>(true, "Applicants fetched successfully.",
				applicationService.getApplicantsForJob(jobId, authentication.getName()))
		);
	}
}
