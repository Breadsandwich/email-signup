package com.email.signup.repository;

import com.email.signup.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, UUID> {
    Optional<Subscriber> findByEmail(String email);
    Optional<Subscriber> findByConfirmationToken(String token);
    boolean existsByEmail(String email);
}
