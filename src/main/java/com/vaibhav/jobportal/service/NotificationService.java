package com.vaibhav.jobportal.service;

import com.vaibhav.jobportal.dto.NotificationResponse;
import com.vaibhav.jobportal.entity.Notification;
import com.vaibhav.jobportal.entity.NotificationType;
import com.vaibhav.jobportal.entity.User;
import com.vaibhav.jobportal.exception.ForbiddenOperationException;
import com.vaibhav.jobportal.exception.UserNotFoundException;
import com.vaibhav.jobportal.repository.NotificationRepository;
import com.vaibhav.jobportal.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final UserService userService;

	public NotificationService(
		NotificationRepository notificationRepository,
		UserRepository userRepository,
		UserService userService
	) {
		this.notificationRepository = notificationRepository;
		this.userRepository = userRepository;
		this.userService = userService;
	}

	public void createNotification(
		User user,
		User actor,
		NotificationType type,
		String title,
		String body,
		String targetPath
	) {
		Notification notification = new Notification();
		notification.setUser(user);
		notification.setActor(actor);
		notification.setType(type);
		notification.setTitle(title);
		notification.setBody(body);
		notification.setTargetPath(targetPath);
		notification.setRead(false);
		notification.setCreatedAt(Instant.now());
		notificationRepository.save(notification);
	}

	public List<NotificationResponse> getNotifications(String email) {
		User user = getUserByEmail(email);
		return notificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
			.limit(40)
			.map(notification -> new NotificationResponse(
				notification.getId(),
				notification.getType(),
				notification.getTitle(),
				notification.getBody(),
				notification.getTargetPath(),
				notification.isRead(),
				notification.getCreatedAt(),
				notification.getActor() == null ? null : userService.toUserResponse(notification.getActor())
			))
			.toList();
	}

	public void markAsRead(String email, Long notificationId) {
		User user = getUserByEmail(email);
		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new UserNotFoundException("Notification not found."));
		if (!notification.getUser().getId().equals(user.getId())) {
			throw new ForbiddenOperationException("You cannot update this notification.");
		}
		notification.setRead(true);
		notificationRepository.save(notification);
	}

	public long getUnreadCount(String email) {
		return notificationRepository.countByUserAndReadFalse(getUserByEmail(email));
	}

	private User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("User not found."));
	}
}
