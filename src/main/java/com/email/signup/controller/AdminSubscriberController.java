package com.email.signup.controller;

import com.email.signup.model.Subscriber;
import com.email.signup.repository.SubscriberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/subscribers")
@Tag(name = "Admin Subscribers", description = "Admin endpoints for managing subscribers")
public class AdminSubscriberController {
    private final SubscriberRepository subscriberRepository;

    public AdminSubscriberController(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    @Operation(
        summary = "List subscribers",
        description = "Returns a paginated list of subscribers. Supports filtering by email and verified status."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of subscribers returned successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<Subscriber>> listSubscribers(
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Page size", example = "20")
        @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "Filter by email address", example = "user@example.com")
        @RequestParam(required = false) String email,
        @Parameter(description = "Filter by verified status", example = "true")
        @RequestParam(required = false) Boolean verified
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Subscriber> result;
        if (email != null && !email.isEmpty()) {
            result = subscriberRepository.findByEmail(email)
                    .map(sub -> new PageImpl<>(List.of(sub), pageable, 1))
                    .orElse(new PageImpl<>(List.of(), pageable, 0));
        } else if (verified != null) {
            result = subscriberRepository.findByVerified(verified, pageable);
        } else {
            result = subscriberRepository.findAll(pageable);
        }
        return ResponseEntity.ok(result);
    }

    @Operation(
        summary = "Delete a subscriber",
        description = "Deletes a subscriber by their unique ID. Returns 204 if successful, 404 if not found."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Subscriber deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Subscriber not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriber(
        @Parameter(description = "UUID of the subscriber to delete", required = true, example = "123e4567-e89b-12d3-a456-426614174000")
        @PathVariable("id") String id
    ) {
        return subscriberRepository.findById(java.util.UUID.fromString(id))
                .map(subscriber -> {
                    subscriberRepository.delete(subscriber);
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).<Void>build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build());
    }
}
