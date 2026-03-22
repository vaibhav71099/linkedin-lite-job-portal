package com.vaibhav.jobportal.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Job {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String description;

	private String company;

	private String location;

	private String employmentType;

	private String seniorityLevel;

	private String salaryRange;

	@ManyToOne
	@JoinColumn(name = "company_profile_id")
	private CompanyProfile companyProfile;

	@ManyToOne
	@JoinColumn(name = "recruiter_id", nullable = false)
	private User recruiter;
}
