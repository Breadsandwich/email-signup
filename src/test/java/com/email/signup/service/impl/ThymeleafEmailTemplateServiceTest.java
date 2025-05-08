package com.email.signup.service.impl;

import com.email.signup.model.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ThymeleafEmailTemplateServiceTest {

    private ThymeleafEmailTemplateService emailTemplateService;
    private Subscriber subscriber;

    @BeforeEach
    void setUp() {
        emailTemplateService = new ThymeleafEmailTemplateService();

        subscriber = mock(Subscriber.class);
        when(subscriber.getName()).thenReturn("John Doe");
    }

    @Test
    void getConfirmationEmailContent_ShouldIncludeNameAndLink() {
        // Given
        String confirmationUrl = "http://localhost:8080/confirm?token=abc123";

        // When
        String content = emailTemplateService.getConfirmationEmailContent(subscriber, confirmationUrl);

        // Then
        assertThat(content)
            .contains("John Doe")
            .contains(confirmationUrl)
            .contains("Confirm Email Address")
            .contains("Welcome to Our Newsletter!");
    }

    @Test
    void getWelcomeEmailContent_ShouldIncludeNameAndLinks() {
        // Given
        String websiteUrl = "http://localhost:8080";
        String unsubscribeUrl = "http://localhost:8080/unsubscribe?email=john@example.com";

        // When
        String content = emailTemplateService.getWelcomeEmailContent(subscriber, websiteUrl, unsubscribeUrl);

        // Then
        assertThat(content)
            .contains("John Doe")
            .contains(websiteUrl)
            .contains(unsubscribeUrl)
            .contains("Welcome Aboard!")
            .contains("You're now officially subscribed");
    }
}
