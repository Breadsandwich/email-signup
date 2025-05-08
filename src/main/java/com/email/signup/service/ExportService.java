package com.email.signup.service;

import com.email.signup.model.Subscriber;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ExportService {
    CompletableFuture<String> exportToCsv(List<Subscriber> subscribers);
    CompletableFuture<String> exportToJson(List<Subscriber> subscribers);
    CompletableFuture<String> uploadToS3(String content, String fileName);
    CompletableFuture<String> generateExportUrl(String fileName);
}
