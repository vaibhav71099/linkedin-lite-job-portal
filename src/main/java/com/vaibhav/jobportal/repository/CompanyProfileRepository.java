package com.vaibhav.jobportal.repository;

import com.vaibhav.jobportal.entity.CompanyProfile;
import com.vaibhav.jobportal.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {

	Optional<CompanyProfile> findByOwner(User owner);

	Optional<CompanyProfile> findByNameIgnoreCase(String name);

	List<CompanyProfile> findByNameContainingIgnoreCaseOrIndustryContainingIgnoreCaseOrderByNameAsc(String name, String industry);
}
