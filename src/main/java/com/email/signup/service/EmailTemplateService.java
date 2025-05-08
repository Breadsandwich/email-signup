package com.email.signup.service;

import com.email.signup.model.Subscriber;

public interface EmailTemplateService {
    String getConfirmationEmailContent(Subscriber subscriber, String confirmationUrl);
    String getWelcomeEmailContent(Subscriber subscriber, String websiteUrl, String unsubscribeUrl);
}
