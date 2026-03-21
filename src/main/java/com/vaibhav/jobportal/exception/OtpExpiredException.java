package com.vaibhav.jobportal.exception;

public class OtpExpiredException extends RuntimeException {
	public OtpExpiredException(String message) {
		super(message);
	}
}
