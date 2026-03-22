package com.vaibhav.jobportal.controller;

import com.vaibhav.jobportal.dto.ApiResponse;
import com.vaibhav.jobportal.dto.PostCommentRequest;
import com.vaibhav.jobportal.dto.PostCommentResponse;
import com.vaibhav.jobportal.dto.PostCreateRequest;
import com.vaibhav.jobportal.dto.PostResponse;
import com.vaibhav.jobportal.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<PostResponse>>> getFeed(Authentication authentication) {
		return ResponseEntity.ok(new ApiResponse<>(
			true,
			"Feed fetched successfully.",
			postService.getFeed(authentication.getName())
		));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<PostResponse>> createPost(
		Authentication authentication,
		@Valid @RequestBody PostCreateRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
			true,
			"Post created successfully.",
			postService.createPost(authentication.getName(), request)
		));
	}

	@PostMapping("/{postId}/react")
	public ResponseEntity<ApiResponse<PostResponse>> toggleReaction(
		Authentication authentication,
		@PathVariable Long postId
	) {
		return ResponseEntity.ok(new ApiResponse<>(
			true,
			"Reaction updated successfully.",
			postService.toggleReaction(authentication.getName(), postId)
		));
	}

	@PostMapping("/{postId}/comments")
	public ResponseEntity<ApiResponse<PostCommentResponse>> addComment(
		Authentication authentication,
		@PathVariable Long postId,
		@Valid @RequestBody PostCommentRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(
			true,
			"Comment added successfully.",
			postService.addComment(authentication.getName(), postId, request)
		));
	}
}
