package com.vaibhav.jobportal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationRequest {

	@NotNull(message = "Job id is required.")
	private Long jobId;
}
