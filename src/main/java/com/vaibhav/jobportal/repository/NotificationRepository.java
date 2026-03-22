package com.vaibhav.jobportal.repository;

import com.vaibhav.jobportal.entity.Notification;
import com.vaibhav.jobportal.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUserOrderByCreatedAtDesc(User user);

	long countByUserAndReadFalse(User user);
}
