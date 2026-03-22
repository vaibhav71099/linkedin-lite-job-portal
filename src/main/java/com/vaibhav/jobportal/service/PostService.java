package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.dto.PostCommentRequest;
import com.vaibhav.jobportal.dto.PostCommentResponse;
import com.vaibhav.jobportal.dto.PostCreateRequest;
import com.vaibhav.jobportal.dto.PostResponse;
import com.vaibhav.jobportal.entity.Post;
import com.vaibhav.jobportal.entity.PostComment;
import com.vaibhav.jobportal.entity.PostReaction;
import com.vaibhav.jobportal.entity.User;
import com.vaibhav.jobportal.exception.PostNotFoundException;
import com.vaibhav.jobportal.repository.PostCommentRepository;
import com.vaibhav.jobportal.repository.PostReactionRepository;
import com.vaibhav.jobportal.repository.PostRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PostService {

	private final PostRepository postRepository;
	private final PostCommentRepository postCommentRepository;
	private final PostReactionRepository postReactionRepository;
	private final UserService userService;
	private final NetworkService networkService;

	public PostService(
		PostRepository postRepository,
		PostCommentRepository postCommentRepository,
		PostReactionRepository postReactionRepository,
		UserService userService,
		NetworkService networkService
	) {
		this.postRepository = postRepository;
		this.postCommentRepository = postCommentRepository;
		this.postReactionRepository = postReactionRepository;
		this.userService = userService;
		this.networkService = networkService;
	}

	public List<PostResponse> getFeed(String email) {
		User currentUser = userService.getUserByEmail(email);
		List<Long> allowedAuthorIds = networkService.getConnectionIdsIncludingSelf(email);

		return postRepository.findAllByOrderByCreatedAtDesc().stream()
			.filter(post -> allowedAuthorIds.contains(post.getAuthor().getId()))
			.limit(40)
			.map(post -> toPostResponse(post, currentUser))
			.toList();
	}

	public PostResponse createPost(String email, PostCreateRequest request) {
		User author = userService.getUserByEmail(email);
		Post post = new Post();
		post.setAuthor(author);
		post.setContent(request.getContent().trim());
		post.setImageUrl(normalize(request.getImageUrl()));
		post.setCreatedAt(Instant.now());
		return toPostResponse(postRepository.save(post), author);
	}

	public PostResponse toggleReaction(String email, Long postId) {
		User currentUser = userService.getUserByEmail(email);
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostNotFoundException("Post not found."));

		postReactionRepository.findByPostIdAndUserId(postId, currentUser.getId())
			.ifPresentOrElse(
				postReactionRepository::delete,
				() -> {
					PostReaction reaction = new PostReaction();
					reaction.setPost(post);
					reaction.setUser(currentUser);
					postReactionRepository.save(reaction);
				}
			);

		return toPostResponse(post, currentUser);
	}

	public PostCommentResponse addComment(String email, Long postId, PostCommentRequest request) {
		User currentUser = userService.getUserByEmail(email);
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostNotFoundException("Post not found."));

		PostComment comment = new PostComment();
		comment.setPost(post);
		comment.setAuthor(currentUser);
		comment.setContent(request.getContent().trim());
		comment.setCreatedAt(Instant.now());
		return toPostCommentResponse(postCommentRepository.save(comment));
	}

	private PostResponse toPostResponse(Post post, User currentUser) {
		List<PostCommentResponse> comments = postCommentRepository.findByPostIdOrderByCreatedAtAsc(post.getId()).stream()
			.map(this::toPostCommentResponse)
			.toList();

		return new PostResponse(
			post.getId(),
			userService.toUserResponse(post.getAuthor()),
			post.getContent(),
			post.getImageUrl(),
			post.getCreatedAt(),
			postReactionRepository.countByPostId(post.getId()),
			comments.size(),
			postReactionRepository.findByPostIdAndUserId(post.getId(), currentUser.getId()).isPresent(),
			comments
		);
	}

	private PostCommentResponse toPostCommentResponse(PostComment comment) {
		return new PostCommentResponse(
			comment.getId(),
			userService.toUserResponse(comment.getAuthor()),
			comment.getContent(),
			comment.getCreatedAt()
		);
	}

	private String normalize(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
