package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.dto.ConversationResponse;
import com.vaibhav.jobportal.dto.MessageRequest;
import com.vaibhav.jobportal.dto.MessageResponse;
import com.vaibhav.jobportal.entity.ConnectionRequestStatus;
import com.vaibhav.jobportal.entity.Conversation;
import com.vaibhav.jobportal.entity.DirectMessage;
import com.vaibhav.jobportal.entity.NotificationType;
import com.vaibhav.jobportal.entity.User;
import com.vaibhav.jobportal.exception.ForbiddenOperationException;
import com.vaibhav.jobportal.exception.UserNotFoundException;
import com.vaibhav.jobportal.repository.ConnectionRequestRepository;
import com.vaibhav.jobportal.repository.ConversationRepository;
import com.vaibhav.jobportal.repository.DirectMessageRepository;
import com.vaibhav.jobportal.repository.UserRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

	private final UserRepository userRepository;
	private final ConversationRepository conversationRepository;
	private final DirectMessageRepository directMessageRepository;
	private final ConnectionRequestRepository connectionRequestRepository;
	private final UserService userService;
	private final NotificationService notificationService;

	public MessagingService(
		UserRepository userRepository,
		ConversationRepository conversationRepository,
		DirectMessageRepository directMessageRepository,
		ConnectionRequestRepository connectionRequestRepository,
		UserService userService,
		NotificationService notificationService
	) {
		this.userRepository = userRepository;
		this.conversationRepository = conversationRepository;
		this.directMessageRepository = directMessageRepository;
		this.connectionRequestRepository = connectionRequestRepository;
		this.userService = userService;
		this.notificationService = notificationService;
	}

	public List<ConversationResponse> getConversations(String email) {
		User currentUser = getUserByEmail(email);
		return conversationRepository.findByUserOneOrUserTwoOrderByUpdatedAtDesc(currentUser, currentUser).stream()
			.sorted(Comparator.comparing(Conversation::getUpdatedAt).reversed())
			.map(conversation -> toConversationResponse(conversation, currentUser))
			.toList();
	}

	public List<MessageResponse> getMessages(String email, Long conversationId) {
		User currentUser = getUserByEmail(email);
		Conversation conversation = getOwnedConversation(currentUser, conversationId);
		List<DirectMessage> messages = directMessageRepository.findByConversationOrderByCreatedAtAsc(conversation);
		messages.stream()
			.filter(message -> message.getReceiver().getId().equals(currentUser.getId()) && !message.isRead())
			.forEach(message -> message.setRead(true));
		directMessageRepository.saveAll(messages);
		return messages.stream().map(this::toMessageResponse).toList();
	}

	public MessageResponse sendMessage(String email, Long userId, MessageRequest request) {
		User sender = getUserByEmail(email);
		User receiver = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("User not found."));

		if (sender.getId().equals(receiver.getId())) {
			throw new ForbiddenOperationException("You cannot message yourself.");
		}
		if (!areConnected(sender, receiver)) {
			throw new ForbiddenOperationException("You can only message accepted connections.");
		}

		Conversation conversation = findOrCreateConversation(sender, receiver);
		DirectMessage message = new DirectMessage();
		message.setConversation(conversation);
		message.setSender(sender);
		message.setReceiver(receiver);
		message.setContent(request.getContent().trim());
		message.setRead(false);
		message.setCreatedAt(Instant.now());
		conversation.setUpdatedAt(message.getCreatedAt());
		conversationRepository.save(conversation);
		DirectMessage savedMessage = directMessageRepository.save(message);

		notificationService.createNotification(
			receiver,
			sender,
			NotificationType.MESSAGE,
			sender.getName() + " sent you a message",
			request.getContent().trim(),
			"/messaging"
		);

		return toMessageResponse(savedMessage);
	}

	private Conversation findOrCreateConversation(User left, User right) {
		User userOne = left.getId() < right.getId() ? left : right;
		User userTwo = left.getId() < right.getId() ? right : left;
		return conversationRepository.findByUserOneAndUserTwo(userOne, userTwo)
			.orElseGet(() -> {
				Conversation conversation = new Conversation();
				conversation.setUserOne(userOne);
				conversation.setUserTwo(userTwo);
				conversation.setUpdatedAt(Instant.now());
				return conversationRepository.save(conversation);
			});
	}

	private boolean areConnected(User left, User right) {
		return connectionRequestRepository.findByRequesterAndReceiver(left, right)
			.map(request -> request.getStatus() == ConnectionRequestStatus.ACCEPTED)
			.orElse(false)
			|| connectionRequestRepository.findByReceiverAndRequester(left, right)
			.map(request -> request.getStatus() == ConnectionRequestStatus.ACCEPTED)
			.orElse(false);
	}

	private Conversation getOwnedConversation(User user, Long conversationId) {
		Conversation conversation = conversationRepository.findById(conversationId)
			.orElseThrow(() -> new UserNotFoundException("Conversation not found."));
		if (!conversation.getUserOne().getId().equals(user.getId())
			&& !conversation.getUserTwo().getId().equals(user.getId())) {
			throw new ForbiddenOperationException("You cannot view this conversation.");
		}
		return conversation;
	}

	private ConversationResponse toConversationResponse(Conversation conversation, User currentUser) {
		User otherParticipant = conversation.getUserOne().getId().equals(currentUser.getId())
			? conversation.getUserTwo()
			: conversation.getUserOne();
		String preview = directMessageRepository.findFirstByConversationOrderByCreatedAtDesc(conversation)
			.map(DirectMessage::getContent)
			.orElse("Start the conversation");
		long unreadCount = directMessageRepository.countByConversationAndReceiverAndReadFalse(conversation, currentUser);
		return new ConversationResponse(
			conversation.getId(),
			userService.toUserResponse(otherParticipant),
			preview,
			conversation.getUpdatedAt(),
			unreadCount
		);
	}

	private MessageResponse toMessageResponse(DirectMessage message) {
		return new MessageResponse(
			message.getId(),
			message.getConversation().getId(),
			userService.toUserResponse(message.getSender()),
			userService.toUserResponse(message.getReceiver()),
			message.getContent(),
			message.isRead(),
			message.getCreatedAt()
		);
	}

	private User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found."));
	}
}
