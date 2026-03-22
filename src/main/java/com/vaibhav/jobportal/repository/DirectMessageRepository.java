package com.vaibhav.jobportal.repository;

import com.vaibhav.jobportal.entity.Conversation;
import com.vaibhav.jobportal.entity.DirectMessage;
import com.vaibhav.jobportal.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {

	List<DirectMessage> findByConversationOrderByCreatedAtAsc(Conversation conversation);

	Optional<DirectMessage> findFirstByConversationOrderByCreatedAtDesc(Conversation conversation);

	long countByConversationAndReceiverAndReadFalse(Conversation conversation, User receiver);
}
