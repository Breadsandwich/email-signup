package com.email.signup.service.impl;

import com.email.signup.dto.SignupRequest;
import com.email.signup.model.Subscriber;
import com.email.signup.repository.SubscriberRepository;
import com.email.signup.service.EmailService;
import com.email.signup.service.SignupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SignupServiceImpl implements SignupService {

    private final SubscriberRepository subscriberRepository;
    private final EmailService emailService;

    public SignupServiceImpl(SubscriberRepository subscriberRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public Subscriber signup(SignupRequest request) {
        // Check if email already exists
        if (subscriberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Create new subscriber
        Subscriber subscriber = new Subscriber(request.getEmail(), request.getName());
        subscriber.setConfirmationToken(generateToken());
        subscriber = subscriberRepository.save(subscriber);

        // Send confirmation email
        emailService.sendConfirmationEmail(subscriber);

        return subscriber;
    }

    @Override
    @Transactional
    public Subscriber confirmEmail(String token) {
        // Find subscriber by token
        Subscriber subscriber = subscriberRepository.findByConfirmationToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Invalid confirmation token"));

        // Check if token is expired (24 hours)
        if (isTokenExpired(subscriber.getCreatedAt())) {
            throw new IllegalArgumentException("Confirmation token has expired");
        }

        // Update subscriber status
        subscriber.setVerified(true);
        subscriber.setConfirmationToken(null); // Invalidate token after use
        subscriber = subscriberRepository.save(subscriber);

        // Send welcome email
        emailService.sendWelcomeEmail(subscriber);

        return subscriber;
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    private boolean isTokenExpired(LocalDateTime createdAt) {
        return createdAt.plusHours(24).isBefore(LocalDateTime.now());
    }
}
