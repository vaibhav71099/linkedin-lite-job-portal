package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.dto.ApplicationResponse;
import com.vaibhav.jobportal.dto.ApplicationRequest;
import com.vaibhav.jobportal.entity.Application;
import com.vaibhav.jobportal.entity.Job;
import com.vaibhav.jobportal.entity.User;
import com.vaibhav.jobportal.exception.ApplicationAlreadyExistsException;
import com.vaibhav.jobportal.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ApplicationService {

	private final ApplicationRepository applicationRepository;
	private final UserService userService;
	private final JobService jobService;

	public ApplicationService(ApplicationRepository applicationRepository,
							  UserService userService,
							  JobService jobService) {
		this.applicationRepository = applicationRepository;
		this.userService = userService;
		this.jobService = jobService;
	}

	public ApplicationResponse applyForJob(String userEmail, ApplicationRequest request) {
		User user = userService.getUserByEmail(userEmail);
		Job job = jobService.getJobById(request.getJobId());

		if (applicationRepository.existsByUserIdAndJobId(user.getId(), job.getId())) {
			throw new ApplicationAlreadyExistsException("You have already applied for this job.");
		}

		Application application = new Application();
		application.setUser(user);
		application.setJob(job);
		application.setAppliedDate(LocalDate.now());

		return toApplicationResponse(applicationRepository.save(application));
	}

	public List<ApplicationResponse> getMyApplications(String userEmail) {
		User user = userService.getUserByEmail(userEmail);
		return applicationRepository.findByUserId(user.getId())
			.stream()
			.map(this::toApplicationResponse)
			.toList();
	}

	public List<ApplicationResponse> getRecruiterApplicants(String recruiterEmail) {
		User recruiter = userService.getUserByEmail(recruiterEmail);
		return applicationRepository.findByJobRecruiterId(recruiter.getId())
			.stream()
			.map(this::toApplicationResponse)
			.toList();
	}

	public List<ApplicationResponse> getApplicantsForJob(Long jobId, String recruiterEmail) {
		Job job = jobService.getJobById(jobId);
		jobService.validateJobOwnership(job, recruiterEmail);

		return applicationRepository.findByJobId(jobId)
			.stream()
			.map(this::toApplicationResponse)
			.toList();
	}

	private ApplicationResponse toApplicationResponse(Application application) {
		return new ApplicationResponse(
			application.getId(),
			application.getUser().getId(),
			application.getUser().getName(),
			application.getUser().getEmail(),
			application.getUser().getSkills(),
			application.getJob().getId(),
			application.getJob().getTitle(),
			application.getJob().getCompany(),
			application.getJob().getLocation(),
			application.getAppliedDate()
		);
	}
}
