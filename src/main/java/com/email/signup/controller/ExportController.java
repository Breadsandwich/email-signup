package com.email.signup.controller;

import com.email.signup.service.ExportService;
import com.email.signup.service.SubscriberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/exports")
public class ExportController {

    private final ExportService exportService;
    private final SubscriberService subscriberService;

    public ExportController(ExportService exportService, SubscriberService subscriberService) {
        this.exportService = exportService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/csv")
    public CompletableFuture<ResponseEntity<String>> exportToCsv() {
        return exportService.exportToCsv(subscriberService.getAllSubscribers())
            .thenApply(url -> ResponseEntity.ok(url));
    }

    @GetMapping("/json")
    public CompletableFuture<ResponseEntity<String>> exportToJson() {
        return exportService.exportToJson(subscriberService.getAllSubscribers())
            .thenApply(url -> ResponseEntity.ok(url));
    }
}
