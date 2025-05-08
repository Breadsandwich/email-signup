package com.email.signup.controller;

import com.email.signup.model.Subscriber;
import com.email.signup.repository.SubscriberRepository;
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
public class AdminSubscriberController {
    private final SubscriberRepository subscriberRepository;

    public AdminSubscriberController(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    @GetMapping
    public ResponseEntity<Page<Subscriber>> listSubscribers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String email,
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriber(@PathVariable("id") String id) {
        return subscriberRepository.findById(java.util.UUID.fromString(id))
                .map(subscriber -> {
                    subscriberRepository.delete(subscriber);
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).<Void>build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build());
    }
}
