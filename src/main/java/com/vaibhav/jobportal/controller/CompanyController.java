package com.vaibhav.jobportal.controller;

import com.vaibhav.jobportal.dto.ApiResponse;
import com.vaibhav.jobportal.dto.CompanyProfileRequest;
import com.vaibhav.jobportal.dto.CompanyProfileResponse;
import com.vaibhav.jobportal.service.CompanyService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

	private final CompanyService companyService;

	public CompanyController(CompanyService companyService) {
		this.companyService = companyService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<CompanyProfileResponse>>> getCompanies(
		@RequestParam(defaultValue = "") String query
	) {
		return ResponseEntity.ok(new ApiResponse<>(true, "Companies fetched successfully.", companyService.getCompanies(query)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<CompanyProfileResponse>> getCompany(@PathVariable Long id) {
		return ResponseEntity.ok(new ApiResponse<>(true, "Company fetched successfully.", companyService.getCompanyById(id)));
	}

	@GetMapping("/mine")
	@PreAuthorize("hasRole('RECRUITER')")
	public ResponseEntity<ApiResponse<CompanyProfileResponse>> getMine(Authentication authentication) {
		return ResponseEntity.ok(new ApiResponse<>(true, "Company profile fetched successfully.", companyService.getMyCompany(authentication.getName())));
	}

	@PostMapping("/mine")
	@PreAuthorize("hasRole('RECRUITER')")
	public ResponseEntity<ApiResponse<CompanyProfileResponse>> createMine(
		Authentication authentication,
		@Valid @RequestBody CompanyProfileRequest request
	) {
		return ResponseEntity.ok(new ApiResponse<>(true, "Company profile saved successfully.", companyService.upsertMyCompany(authentication.getName(), request)));
	}

	@PutMapping("/mine")
	@PreAuthorize("hasRole('RECRUITER')")
	public ResponseEntity<ApiResponse<CompanyProfileResponse>> updateMine(
		Authentication authentication,
		@Valid @RequestBody CompanyProfileRequest request
	) {
		return ResponseEntity.ok(new ApiResponse<>(true, "Company profile updated successfully.", companyService.upsertMyCompany(authentication.getName(), request)));
	}
}
