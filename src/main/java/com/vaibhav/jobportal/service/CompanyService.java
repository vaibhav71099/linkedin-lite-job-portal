package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.dto.CompanyProfileRequest;
import com.vaibhav.jobportal.dto.CompanyProfileResponse;
import com.vaibhav.jobportal.entity.CompanyProfile;
import com.vaibhav.jobportal.entity.User;
import com.vaibhav.jobportal.exception.UserAlreadyExistsException;
import com.vaibhav.jobportal.exception.UserNotFoundException;
import com.vaibhav.jobportal.repository.CompanyProfileRepository;
import com.vaibhav.jobportal.repository.JobRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

	private final CompanyProfileRepository companyProfileRepository;
	private final JobRepository jobRepository;
	private final UserService userService;

	public CompanyService(
		CompanyProfileRepository companyProfileRepository,
		JobRepository jobRepository,
		UserService userService
	) {
		this.companyProfileRepository = companyProfileRepository;
		this.jobRepository = jobRepository;
		this.userService = userService;
	}

	public List<CompanyProfileResponse> getCompanies(String query) {
		String normalized = query == null ? "" : query.trim();
		return companyProfileRepository.findByNameContainingIgnoreCaseOrIndustryContainingIgnoreCaseOrderByNameAsc(
			normalized,
			normalized
		).stream().map(this::toResponse).toList();
	}

	public CompanyProfileResponse getCompanyById(Long id) {
		return toResponse(companyProfileRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException("Company not found.")));
	}

	public CompanyProfileResponse getMyCompany(String email) {
		User owner = userService.getUserByEmail(email);
		CompanyProfile existing = companyProfileRepository.findByOwner(owner).orElse(null);
		if (existing == null) {
			return new CompanyProfileResponse(null, "", "", "", "", "", "", "", "", owner.getId(), owner.getName(), 0);
		}
		return toResponse(existing);
	}

	public CompanyProfileResponse upsertMyCompany(String email, CompanyProfileRequest request) {
		User owner = userService.getUserByEmail(email);
		CompanyProfile existing = companyProfileRepository.findByOwner(owner).orElse(null);
		CompanyProfile byName = companyProfileRepository.findByNameIgnoreCase(request.getName().trim()).orElse(null);
		if (byName != null && (existing == null || !byName.getId().equals(existing.getId()))) {
			throw new UserAlreadyExistsException("A company with this name already exists.");
		}

		CompanyProfile profile = existing == null ? new CompanyProfile() : existing;
		profile.setOwner(owner);
		profile.setName(request.getName().trim());
		profile.setSlogan(normalize(request.getSlogan()));
		profile.setIndustry(normalize(request.getIndustry()));
		profile.setAbout(normalize(request.getAbout()));
		profile.setSize(normalize(request.getSize()));
		profile.setHeadquarters(normalize(request.getHeadquarters()));
		profile.setWebsite(normalize(request.getWebsite()));
		profile.setCoverImageUrl(normalize(request.getCoverImageUrl()));
		return toResponse(companyProfileRepository.save(profile));
	}

	public CompanyProfile getOwnedCompanyEntity(String email) {
		User owner = userService.getUserByEmail(email);
		return companyProfileRepository.findByOwner(owner).orElse(null);
	}

	public CompanyProfileResponse toResponse(CompanyProfile profile) {
		return new CompanyProfileResponse(
			profile.getId(),
			profile.getName(),
			profile.getSlogan(),
			profile.getIndustry(),
			profile.getAbout(),
			profile.getSize(),
			profile.getHeadquarters(),
			profile.getWebsite(),
			profile.getCoverImageUrl(),
			profile.getOwner().getId(),
			profile.getOwner().getName(),
			jobRepository.countByCompanyProfileId(profile.getId())
		);
	}

	private String normalize(String value) {
		return value == null ? "" : value.trim();
	}
}
