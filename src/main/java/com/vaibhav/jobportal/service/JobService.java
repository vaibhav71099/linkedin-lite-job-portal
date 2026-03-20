package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.dto.JobRequest;
import com.vaibhav.jobportal.dto.JobResponse;
import com.vaibhav.jobportal.dto.PagedResponse;
import com.vaibhav.jobportal.entity.Job;
import com.vaibhav.jobportal.entity.User;
import com.vaibhav.jobportal.exception.ForbiddenOperationException;
import com.vaibhav.jobportal.exception.JobNotFoundException;
import com.vaibhav.jobportal.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

	private final JobRepository jobRepository;
	private final UserService userService;

	public JobService(JobRepository jobRepository, UserService userService) {
		this.jobRepository = jobRepository;
		this.userService = userService;
	}

	public JobResponse createJob(JobRequest request, String recruiterEmail) {
		User recruiter = userService.getUserByEmail(recruiterEmail);

		Job job = new Job();
		job.setTitle(request.getTitle());
		job.setDescription(request.getDescription());
		job.setCompany(request.getCompany());
		job.setLocation(request.getLocation());
		job.setRecruiter(recruiter);
		return toJobResponse(jobRepository.save(job));
	}

	public PagedResponse<JobResponse> getAllJobs(String title, String location, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
		Page<Job> jobsPage = jobRepository.findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(
			normalizeFilter(title),
			normalizeFilter(location),
			pageable
		);

		return new PagedResponse<>(
			jobsPage.getContent().stream().map(this::toJobResponse).toList(),
			jobsPage.getNumber(),
			jobsPage.getSize(),
			jobsPage.getTotalElements(),
			jobsPage.getTotalPages(),
			jobsPage.isFirst(),
			jobsPage.isLast()
		);
	}

	public Job getJobById(Long id) {
		return jobRepository.findById(id)
			.orElseThrow(() -> new JobNotFoundException("Job not found with id: " + id));
	}

	public List<JobResponse> getRecruiterJobs(String recruiterEmail) {
		User recruiter = userService.getUserByEmail(recruiterEmail);
		return jobRepository.findByRecruiterId(recruiter.getId())
			.stream()
			.map(this::toJobResponse)
			.toList();
	}

	public JobResponse updateJob(Long id, JobRequest request, String recruiterEmail) {
		Job existingJob = getJobById(id);
		validateJobOwnership(existingJob, recruiterEmail);

		existingJob.setTitle(request.getTitle());
		existingJob.setDescription(request.getDescription());
		existingJob.setCompany(request.getCompany());
		existingJob.setLocation(request.getLocation());

		return toJobResponse(jobRepository.save(existingJob));
	}

	public void deleteJob(Long id, String recruiterEmail) {
		Job existingJob = getJobById(id);
		validateJobOwnership(existingJob, recruiterEmail);

		jobRepository.delete(existingJob);
	}

	public void validateJobOwnership(Job job, String recruiterEmail) {
		User currentUser = userService.getUserByEmail(recruiterEmail);

		if (job.getRecruiter() == null || !job.getRecruiter().getId().equals(currentUser.getId())) {
			throw new ForbiddenOperationException("You are not allowed to manage this job.");
		}
	}

	private String normalizeFilter(String value) {
		return value == null ? "" : value.trim();
	}

	private JobResponse toJobResponse(Job job) {
		return new JobResponse(
			job.getId(),
			job.getTitle(),
			job.getDescription(),
			job.getCompany(),
			job.getLocation(),
			job.getRecruiter() == null ? null : job.getRecruiter().getId(),
			job.getRecruiter() == null ? null : job.getRecruiter().getName()
		);
	}
}
