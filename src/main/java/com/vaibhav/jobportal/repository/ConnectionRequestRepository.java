package com.vaibhav.jobportal.repository;

import com.vaibhav.jobportal.entity.ConnectionRequest;
import com.vaibhav.jobportal.entity.ConnectionRequestStatus;
import com.vaibhav.jobportal.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {

	List<ConnectionRequest> findByReceiverAndStatusOrderByCreatedAtDesc(User receiver, ConnectionRequestStatus status);

	List<ConnectionRequest> findByStatusAndRequesterOrStatusAndReceiver(
		ConnectionRequestStatus requesterStatus,
		User requester,
		ConnectionRequestStatus receiverStatus,
		User receiver
	);

	Optional<ConnectionRequest> findByRequesterAndReceiver(User requester, User receiver);

	Optional<ConnectionRequest> findByReceiverAndRequester(User receiver, User requester);
}
