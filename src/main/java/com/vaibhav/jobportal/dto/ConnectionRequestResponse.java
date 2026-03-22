package com.vaibhav.jobportal.dto;

import com.vaibhav.jobportal.entity.ConnectionRequestStatus;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConnectionRequestResponse {

	private Long id;
	private UserResponse requester;
	private UserResponse receiver;
	private ConnectionRequestStatus status;
	private Instant createdAt;
}
