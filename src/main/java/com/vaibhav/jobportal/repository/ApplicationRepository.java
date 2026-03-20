package com.vaibhav.jobportal.repository;

import com.vaibhav.jobportal.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

	boolean existsByUserIdAndJobId(Long userId, Long jobId);

	List<Application> findByUserId(Long userId);

	List<Application> findByJobId(Long jobId);

	List<Application> findByJobRecruiterId(Long recruiterId);
}
