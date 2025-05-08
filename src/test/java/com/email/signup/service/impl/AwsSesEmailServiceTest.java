package com.email.signup.service.impl;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.email.signup.model.Subscriber;
import com.email.signup.service.EmailTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AwsSesEmailServiceTest {

    @Mock
    private AmazonSimpleEmailService sesClient;

    @Mock
    private EmailTemplateService emailTemplateService;

    @Mock
    private Subscriber subscriber;

    private AwsSesEmailService emailService;
    private static final String FROM_EMAIL = "test@example.com";
    private static final String APP_URL = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        emailService = new AwsSesEmailService(sesClient, emailTemplateService);
        ReflectionTestUtils.setField(emailService, "fromEmail", FROM_EMAIL);
        ReflectionTestUtils.setField(emailService, "appUrl", APP_URL);
    }

    @Test
    void sendConfirmationEmail_ShouldSendEmailWithCorrectContent() {
        // Given
        String subscriberEmail = "subscriber@example.com";
        String confirmationToken = "test-token";
        when(subscriber.getEmail()).thenReturn(subscriberEmail);
        when(subscriber.getConfirmationToken()).thenReturn(confirmationToken);

        String expectedConfirmationUrl = APP_URL + "/confirm?token=" + confirmationToken;
        when(emailTemplateService.getConfirmationEmailContent(subscriber, expectedConfirmationUrl))
            .thenReturn("<html>Confirmation Email</html>");

        // When
        emailService.sendConfirmationEmail(subscriber);

        // Then
        ArgumentCaptor<SendEmailRequest> requestCaptor = ArgumentCaptor.forClass(SendEmailRequest.class);
        verify(sesClient).sendEmail(requestCaptor.capture());

        SendEmailRequest request = requestCaptor.getValue();
        assertThat(request.getSource()).isEqualTo(FROM_EMAIL);
        assertThat(request.getDestination().getToAddresses()).contains(subscriberEmail);
        assertThat(request.getMessage().getSubject().getData()).contains("Confirm");
        assertThat(request.getMessage().getBody().getHtml().getData()).contains("Confirmation Email");
    }

    @Test
    void sendWelcomeEmail_ShouldSendEmailWithCorrectContent() {
        // Given
        String subscriberEmail = "subscriber@example.com";
        when(subscriber.getEmail()).thenReturn(subscriberEmail);

        String expectedWebsiteUrl = APP_URL;
        String expectedUnsubscribeUrl = APP_URL + "/unsubscribe?email=" + subscriberEmail;
        when(emailTemplateService.getWelcomeEmailContent(subscriber, expectedWebsiteUrl, expectedUnsubscribeUrl))
            .thenReturn("<html>Welcome Email</html>");

        // When
        emailService.sendWelcomeEmail(subscriber);

        // Then
        ArgumentCaptor<SendEmailRequest> requestCaptor = ArgumentCaptor.forClass(SendEmailRequest.class);
        verify(sesClient).sendEmail(requestCaptor.capture());

        SendEmailRequest request = requestCaptor.getValue();
        assertThat(request.getSource()).isEqualTo(FROM_EMAIL);
        assertThat(request.getDestination().getToAddresses()).contains(subscriberEmail);
        assertThat(request.getMessage().getSubject().getData()).contains("Welcome");
        assertThat(request.getMessage().getBody().getHtml().getData()).contains("Welcome Email");
    }

    @Test
    void sendEmail_ShouldHandleAwsException() {
        // Given
        String subscriberEmail = "subscriber@example.com";
        String confirmationToken = "test-token";
        when(subscriber.getEmail()).thenReturn(subscriberEmail);
        when(subscriber.getConfirmationToken()).thenReturn(confirmationToken);

        when(sesClient.sendEmail(any())).thenThrow(new RuntimeException("AWS Error"));
        when(emailTemplateService.getConfirmationEmailContent(any(), anyString()))
            .thenReturn("<html>Test</html>");

        // When/Then
        org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> emailService.sendConfirmationEmail(subscriber)
        );
    }
}
