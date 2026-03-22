package com.vaibhav.jobportal.repository;

import com.vaibhav.jobportal.entity.Conversation;
import com.vaibhav.jobportal.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

	Optional<Conversation> findByUserOneAndUserTwo(User userOne, User userTwo);

	List<Conversation> findByUserOneOrUserTwoOrderByUpdatedAtDesc(User userOne, User userTwo);
}
