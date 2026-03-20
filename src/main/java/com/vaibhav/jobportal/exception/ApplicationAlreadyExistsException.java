package com.vaibhav.jobportal.exception;

public class ApplicationAlreadyExistsException extends RuntimeException {

	public ApplicationAlreadyExistsException(String message) {
		super(message);
	}
}
