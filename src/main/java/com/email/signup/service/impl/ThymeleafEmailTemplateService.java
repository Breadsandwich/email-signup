package com.email.signup.service.impl;

import com.email.signup.model.Subscriber;
import com.email.signup.service.EmailTemplateService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class ThymeleafEmailTemplateService implements EmailTemplateService {

    @Override
    public String getConfirmationEmailContent(Subscriber subscriber, String confirmationUrl) {
        String template = loadTemplate("templates/email/confirmation.html");
        return replaceVariables(template, Map.of(
            "name", subscriber.getName(),
            "confirmationLink", confirmationUrl
        ));
    }

    @Override
    public String getWelcomeEmailContent(Subscriber subscriber, String websiteUrl, String unsubscribeUrl) {
        String template = loadTemplate("templates/email/welcome.html");
        return replaceVariables(template, Map.of(
            "name", subscriber.getName(),
            "websiteUrl", websiteUrl,
            "unsubscribeUrl", unsubscribeUrl
        ));
    }

    private String loadTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load email template: " + path, e);
        }
    }

    private String replaceVariables(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
