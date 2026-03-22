package com.vaibhav.jobportal.exception;

import com.vaibhav.jobportal.dto.ApiResponse;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ApiResponse<>(false, ex.getMessage(), null));
	}

	@ExceptionHandler(JobNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleJobNotFound(JobNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ApiResponse<>(false, ex.getMessage(), null));
	}

	@ExceptionHandler(PostNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handlePostNotFound(PostNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ApiResponse<>(false, ex.getMessage(), null));
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(InvalidCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(new ApiResponse<>(false, ex.getMessage(), null));
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new ApiResponse<>(false, ex.getMessage(), null));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(new ApiResponse<>(false, "Invalid email or password.", null));
	}

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(new ApiResponse<>(false, "Invalid or expired token.", null));
	}

	@ExceptionHandler(ApplicationAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Void>> handleApplicationAlreadyExists(ApplicationAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new ApiResponse<>(false, ex.getMessage(), null));
	}

	@ExceptionHandler(InvalidRoleException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidRole(InvalidRoleException ex) {
		return ResponseEntity.badRequest()
			.body(new ApiResponse<>(false, ex.getMessage(), null));
	}

	@ExceptionHandler(PendingRegistrationNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handlePendingRegistrationNotFound(PendingRegistrationNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ApiResponse<>(false, ex.getMessage(), null));
	}

	@ExceptionHandler(OtpInvalidException.class)
	public ResponseEntity<ApiResponse<Void>> handleOtpInvalid(OtpInvalidException ex) {
		return ResponseEntity.badRequest()
			.body(new ApiResponse<>(false, ex.getMessage(), null));
	}

	@ExceptionHandler(OtpExpiredException.class)
	public ResponseEntity<ApiResponse<Void>> handleOtpExpired(OtpExpiredException ex) {
		return ResponseEntity.badRequest()
			.body(new ApiResponse<>(false, ex.getMessage(), null));
	}

	@ExceptionHandler(OtpDeliveryException.class)
	public ResponseEntity<ApiResponse<Void>> handleOtpDelivery(OtpDeliveryException ex) {
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
			.body(new ApiResponse<>(false, "Unable to send OTP right now. Please try again shortly.", null));
	}

	@ExceptionHandler(ForbiddenOperationException.class)
	public ResponseEntity<ApiResponse<Void>> handleForbiddenOperation(ForbiddenOperationException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(new ApiResponse<>(false, ex.getMessage(), null));
	}

	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ApiResponse<Void>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(new ApiResponse<>(false, "You are not allowed to access this resource.", null));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
		MethodArgumentNotValidException ex,
		HttpHeaders headers,
		HttpStatusCode status,
		WebRequest request
	) {
		String message = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> error.getField() + ": " + error.getDefaultMessage())
			.collect(Collectors.joining(", "));

		return ResponseEntity.badRequest().body(new ApiResponse<>(false, message, null));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ApiResponse<>(false, "Something went wrong. Please try again.", null));
	}
}
