package com.email.signup.service;

import com.email.signup.model.Subscriber;

public interface EmailService {
    void sendConfirmationEmail(Subscriber subscriber);
    void sendWelcomeEmail(Subscriber subscriber);
}
