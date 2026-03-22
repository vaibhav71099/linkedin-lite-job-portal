package com.vaibhav.jobportal.repository;

import com.vaibhav.jobportal.entity.PostComment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

	List<PostComment> findByPostIdOrderByCreatedAtAsc(Long postId);

	long countByPostId(Long postId);
}
