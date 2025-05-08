package com.email.signup.service.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.email.signup.model.Subscriber;
import com.email.signup.service.EmailService;
import com.email.signup.service.EmailTemplateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// AWS SES implementation of email service
@Service
public class AwsSesEmailService implements EmailService {

    private final AmazonSimpleEmailService sesClient;
    private final EmailTemplateService emailTemplateService;

    // Email address to send from
    @Value("${aws.ses.from-email}")
    private String fromEmail;

    // Base URL for generating links
    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    public AwsSesEmailService(AmazonSimpleEmailService sesClient, EmailTemplateService emailTemplateService) {
        this.sesClient = sesClient;
        this.emailTemplateService = emailTemplateService;
    }

    // Sends initial confirmation email with verification link
    @Override
    public void sendConfirmationEmail(Subscriber subscriber) {
           String confirmationUrl = appUrl + "/api/confirm?token=" + subscriber.getConfirmationToken();
        String htmlBody = emailTemplateService.getConfirmationEmailContent(subscriber, confirmationUrl);
        String subject = "Confirm your email subscription";

        sendEmail(subscriber.getEmail(), subject, htmlBody);
    }

    // Sends welcome email after subscription is confirmed
    @Override
    public void sendWelcomeEmail(Subscriber subscriber) {
        String websiteUrl = appUrl;
        String unsubscribeUrl = appUrl + "/unsubscribe?email=" + subscriber.getEmail();
        String htmlBody = emailTemplateService.getWelcomeEmailContent(subscriber, websiteUrl, unsubscribeUrl);
        String subject = "Welcome to our newsletter!";

        sendEmail(subscriber.getEmail(), subject, htmlBody);
    }

    // Helper method to send email via AWS SES
    private void sendEmail(String to, String subject, String htmlBody) {
        SendEmailRequest request = new SendEmailRequest()
            .withDestination(new Destination().withToAddresses(to))
            .withMessage(new Message()
                .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBody)))
                .withSubject(new Content().withCharset("UTF-8").withData(subject)))
            .withSource(fromEmail);

        try {
            sesClient.sendEmail(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email to " + to, e);
        }
    }
}
