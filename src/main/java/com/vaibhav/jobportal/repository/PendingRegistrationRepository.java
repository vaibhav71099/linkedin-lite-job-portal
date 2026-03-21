package com.vaibhav.jobportal.repository;

import com.vaibhav.jobportal.entity.PendingRegistration;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, Long> {

	Optional<PendingRegistration> findByEmail(String email);

	Optional<PendingRegistration> findByPhone(String phone);
}
