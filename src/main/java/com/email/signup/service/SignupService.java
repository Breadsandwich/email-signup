package com.email.signup.service;

import com.email.signup.dto.SignupRequest;
import com.email.signup.model.Subscriber;

public interface SignupService {
    /**
     * Process a new signup request
     * @param request The signup request containing email and name
     * @return The created subscriber
     * @throws IllegalArgumentException if email already exists
     */
    Subscriber signup(SignupRequest request);

    /**
     * Confirm a subscriber's email address
     * @param token The confirmation token
     * @return The confirmed subscriber
     * @throws IllegalArgumentException if token is invalid or expired
     */
    Subscriber confirmEmail(String token);
}
