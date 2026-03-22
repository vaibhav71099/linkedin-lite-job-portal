package com.vaibhav.jobportal.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostResponse {

	private Long id;
	private UserResponse author;
	private String content;
	private String imageUrl;
	private Instant createdAt;
	private long reactionCount;
	private long commentCount;
	private boolean reactedByCurrentUser;
	private List<PostCommentResponse> comments;
}
