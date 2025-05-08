package com.email.signup.controller;

import com.email.signup.dto.SignupRequest;
import com.email.signup.model.Subscriber;
import com.email.signup.service.SignupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Signup", description = "Email signup and confirmation endpoints")
public class SignupController {

    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @Operation(
        summary = "Sign up for email subscription",
        description = "Creates a new subscriber and sends a confirmation email"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Signup successful, confirmation email sent",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input or email already registered",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Map.class))
        )
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
        @Parameter(description = "Signup request containing email and name", required = true)
        @Valid @RequestBody SignupRequest request
    ) {
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

    @Operation(
        summary = "Confirm email subscription",
        description = "Confirms the email subscription using the token from the confirmation email"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Email confirmed successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid or expired token",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Map.class))
        )
    })
    @GetMapping("/confirm")
    public ResponseEntity<?> confirmEmail(
        @Parameter(description = "Confirmation token from the email", required = true)
        @RequestParam String token
    ) {
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
