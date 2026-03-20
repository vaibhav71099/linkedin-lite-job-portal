package com.vaibhav.jobportal.repository;

import com.vaibhav.jobportal.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {

	List<Job> findByRecruiterId(Long recruiterId);

	Page<Job> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(
		String title,
		String location,
		Pageable pageable
	);
}
