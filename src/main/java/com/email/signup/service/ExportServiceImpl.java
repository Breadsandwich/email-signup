package com.email.signup.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.email.signup.model.Subscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ExportServiceImpl implements ExportService {

    private final AmazonS3 s3Client;
    private final ObjectMapper objectMapper;
    private final String bucketName;

    public ExportServiceImpl(AmazonS3 s3Client,
                           @Value("${aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule for LocalDateTime
    }

    @Async
    @Override
    public CompletableFuture<String> exportToCsv(List<Subscriber> subscribers) {
        try (StringWriter writer = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(writer)) {

            // Write header
            csvWriter.writeNext(new String[]{"ID", "Email", "Name", "Verified", "Created At"});

            // Write data
            for (Subscriber subscriber : subscribers) {
                csvWriter.writeNext(new String[]{
                    subscriber.getId().toString(),
                    subscriber.getEmail(),
                    subscriber.getName(),
                    String.valueOf(subscriber.isVerified()),
                    subscriber.getCreatedAt().toString()
                });
            }

            String csvContent = writer.toString();
            String fileName = "subscribers_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
            return uploadToS3(csvContent, fileName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export to CSV", e);
        }
    }

    @Async
    @Override
    public CompletableFuture<String> exportToJson(List<Subscriber> subscribers) {
        try {
            String jsonContent = objectMapper.writeValueAsString(subscribers);
            String fileName = "subscribers_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".json";
            return uploadToS3(jsonContent, fileName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to export to JSON", e);
        }
    }

    @Async
    @Override
    public CompletableFuture<String> uploadToS3(String content, String fileName) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(content.getBytes().length);

            PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName,
                "exports/" + fileName,
                new java.io.ByteArrayInputStream(content.getBytes()),
                metadata
            );

            s3Client.putObject(putObjectRequest);
            return generateExportUrl(fileName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload to S3", e);
        }
    }

    @Async
    @Override
    public CompletableFuture<String> generateExportUrl(String fileName) {
        try {
            // Generate a pre-signed URL that expires in 1 hour
            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60; // 1 hour
            expiration.setTime(expTimeMillis);

            java.net.URL url = s3Client.generatePresignedUrl(
                bucketName,
                "exports/" + fileName,
                expiration
            );

            return CompletableFuture.completedFuture(url.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate export URL", e);
        }
    }
}
