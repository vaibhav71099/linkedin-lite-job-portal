package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.dto.ConnectionRequestResponse;
import com.vaibhav.jobportal.dto.UserResponse;
import com.vaibhav.jobportal.entity.ConnectionRequest;
import com.vaibhav.jobportal.entity.ConnectionRequestStatus;
import com.vaibhav.jobportal.entity.User;
import com.vaibhav.jobportal.exception.ForbiddenOperationException;
import com.vaibhav.jobportal.exception.UserAlreadyExistsException;
import com.vaibhav.jobportal.exception.UserNotFoundException;
import com.vaibhav.jobportal.repository.ConnectionRequestRepository;
import com.vaibhav.jobportal.repository.UserRepository;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class NetworkService {

	private final UserRepository userRepository;
	private final ConnectionRequestRepository connectionRequestRepository;
	private final UserService userService;

	public NetworkService(
		UserRepository userRepository,
		ConnectionRequestRepository connectionRequestRepository,
		UserService userService
	) {
		this.userRepository = userRepository;
		this.connectionRequestRepository = connectionRequestRepository;
		this.userService = userService;
	}

	public List<UserResponse> getDiscoverPeople(String email) {
		User currentUser = getUserByEmail(email);
		List<Long> connectedIds = getConnectedUsers(currentUser).stream()
			.map(User::getId)
			.toList();

		return userRepository.findAll().stream()
			.filter(user -> !user.getId().equals(currentUser.getId()))
			.filter(user -> !connectedIds.contains(user.getId()))
			.filter(user -> !hasPendingRequestBetween(currentUser, user))
			.sorted(Comparator.comparing(User::getName, String.CASE_INSENSITIVE_ORDER))
			.limit(12)
			.map(userService::toUserResponse)
			.toList();
	}

	public List<ConnectionRequestResponse> getPendingInvitations(String email) {
		User currentUser = getUserByEmail(email);
		return connectionRequestRepository.findByReceiverAndStatusOrderByCreatedAtDesc(
			currentUser,
			ConnectionRequestStatus.PENDING
		).stream()
			.map(this::toConnectionRequestResponse)
			.toList();
	}

	public List<UserResponse> getConnections(String email) {
		User currentUser = getUserByEmail(email);
		return getConnectedUsers(currentUser).stream()
			.sorted(Comparator.comparing(User::getName, String.CASE_INSENSITIVE_ORDER))
			.map(userService::toUserResponse)
			.toList();
	}

	public ConnectionRequestResponse sendConnectionRequest(String email, Long userId) {
		User requester = getUserByEmail(email);
		User receiver = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException("User not found."));

		if (requester.getId().equals(receiver.getId())) {
			throw new ForbiddenOperationException("You cannot connect with yourself.");
		}
		if (isConnected(requester, receiver)) {
			throw new UserAlreadyExistsException("You are already connected.");
		}

		ConnectionRequest existing = connectionRequestRepository.findByRequesterAndReceiver(requester, receiver)
			.orElseGet(() -> connectionRequestRepository.findByReceiverAndRequester(requester, receiver).orElse(null));

		if (existing != null) {
			if (existing.getStatus() == ConnectionRequestStatus.PENDING) {
				throw new UserAlreadyExistsException("A pending invitation already exists.");
			}
			if (existing.getStatus() == ConnectionRequestStatus.ACCEPTED) {
				throw new UserAlreadyExistsException("You are already connected.");
			}
			existing.setRequester(requester);
			existing.setReceiver(receiver);
			existing.setStatus(ConnectionRequestStatus.PENDING);
			existing.setCreatedAt(Instant.now());
			existing.setRespondedAt(null);
			return toConnectionRequestResponse(connectionRequestRepository.save(existing));
		}

		ConnectionRequest request = new ConnectionRequest();
		request.setRequester(requester);
		request.setReceiver(receiver);
		request.setStatus(ConnectionRequestStatus.PENDING);
		request.setCreatedAt(Instant.now());
		return toConnectionRequestResponse(connectionRequestRepository.save(request));
	}

	public ConnectionRequestResponse acceptConnectionRequest(String email, Long requestId) {
		ConnectionRequest request = getOwnedPendingRequest(email, requestId);
		request.setStatus(ConnectionRequestStatus.ACCEPTED);
		request.setRespondedAt(Instant.now());
		return toConnectionRequestResponse(connectionRequestRepository.save(request));
	}

	public ConnectionRequestResponse ignoreConnectionRequest(String email, Long requestId) {
		ConnectionRequest request = getOwnedPendingRequest(email, requestId);
		request.setStatus(ConnectionRequestStatus.IGNORED);
		request.setRespondedAt(Instant.now());
		return toConnectionRequestResponse(connectionRequestRepository.save(request));
	}

	private ConnectionRequest getOwnedPendingRequest(String email, Long requestId) {
		User currentUser = getUserByEmail(email);
		ConnectionRequest request = connectionRequestRepository.findById(requestId)
			.orElseThrow(() -> new UserNotFoundException("Invitation not found."));
		if (!request.getReceiver().getId().equals(currentUser.getId())) {
			throw new ForbiddenOperationException("You are not allowed to manage this invitation.");
		}
		if (request.getStatus() != ConnectionRequestStatus.PENDING) {
			throw new ForbiddenOperationException("This invitation is no longer pending.");
		}
		return request;
	}

	private boolean hasPendingRequestBetween(User left, User right) {
		return connectionRequestRepository.findByRequesterAndReceiver(left, right)
			.map(request -> request.getStatus() == ConnectionRequestStatus.PENDING)
			.orElse(false)
			|| connectionRequestRepository.findByReceiverAndRequester(left, right)
			.map(request -> request.getStatus() == ConnectionRequestStatus.PENDING)
			.orElse(false);
	}

	private boolean isConnected(User left, User right) {
		return connectionRequestRepository.findByRequesterAndReceiver(left, right)
			.map(request -> request.getStatus() == ConnectionRequestStatus.ACCEPTED)
			.orElse(false)
			|| connectionRequestRepository.findByReceiverAndRequester(left, right)
			.map(request -> request.getStatus() == ConnectionRequestStatus.ACCEPTED)
			.orElse(false);
	}

	private List<User> getConnectedUsers(User currentUser) {
		return connectionRequestRepository.findByStatusAndRequesterOrStatusAndReceiver(
			ConnectionRequestStatus.ACCEPTED,
			currentUser,
			ConnectionRequestStatus.ACCEPTED,
			currentUser
		).stream()
			.flatMap(request -> Stream.of(request.getRequester(), request.getReceiver()))
			.filter(user -> !user.getId().equals(currentUser.getId()))
			.distinct()
			.toList();
	}

	private User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found."));
	}

	private ConnectionRequestResponse toConnectionRequestResponse(ConnectionRequest request) {
		return new ConnectionRequestResponse(
			request.getId(),
			userService.toUserResponse(request.getRequester()),
			userService.toUserResponse(request.getReceiver()),
			request.getStatus(),
			request.getCreatedAt()
		);
	}
}
