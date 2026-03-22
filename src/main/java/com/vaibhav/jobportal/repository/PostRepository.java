package com.vaibhav.jobportal.repository;

import com.vaibhav.jobportal.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findAllByOrderByCreatedAtDesc();

	List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
}
