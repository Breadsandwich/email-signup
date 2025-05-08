package com.email.signup.controller;

import com.email.signup.dto.SignupRequest;
import com.email.signup.model.Subscriber;
import com.email.signup.service.SignupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            Subscriber subscriber = signupService.signup(request);
            return ResponseEntity.ok(Map.of(
                "message", "Please check your email to confirm your subscription",
                "email", subscriber.getEmail()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam String token) {
        try {
            Subscriber subscriber = signupService.confirmEmail(token);
            return ResponseEntity.ok(Map.of(
                "message", "Email confirmed successfully",
                "email", subscriber.getEmail()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
