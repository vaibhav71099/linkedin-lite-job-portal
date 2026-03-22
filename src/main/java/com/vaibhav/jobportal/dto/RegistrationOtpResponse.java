package com.vaibhav.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegistrationOtpResponse {

	private boolean phoneOtpRequired;
}
