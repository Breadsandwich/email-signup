package com.email.signup.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.email.signup.model.Subscriber;
import com.email.signup.service.ExportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExportServiceImplTest {
    private AmazonS3 s3Client;
    private ExportServiceImpl exportService;

    @BeforeEach
    void setUp() {
        s3Client = mock(AmazonS3.class);
        exportService = new ExportServiceImpl(s3Client, "test-bucket");
    }

    @Test
    void exportToCsv_shouldUploadCsvToS3() throws Exception {
        Subscriber s1 = spy(new Subscriber("test1@example.com", "Test User 1"));
        s1.setVerified(true);
        Subscriber s2 = spy(new Subscriber("test2@example.com", "Test User 2"));
        s2.setVerified(false);
        java.util.UUID uuid1 = java.util.UUID.randomUUID();
        java.util.UUID uuid2 = java.util.UUID.randomUUID();
        when(s1.getId()).thenReturn(uuid1);
        when(s2.getId()).thenReturn(uuid2);
        List<Subscriber> subscribers = Arrays.asList(s1, s2);
        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(null);
        when(s3Client.generatePresignedUrl(any(), any(), any())).thenReturn(new java.net.URL("http://example.com/test.csv"));

        CompletableFuture<String> result = exportService.exportToCsv(subscribers);
        String url = result.get();

        assertEquals("http://example.com/test.csv", url);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    void exportToJson_shouldUploadJsonToS3() throws Exception {
        Subscriber s1 = new Subscriber("test1@example.com", "Test User 1");
        s1.setVerified(true);
        Subscriber s2 = new Subscriber("test2@example.com", "Test User 2");
        s2.setVerified(false);
        List<Subscriber> subscribers = Arrays.asList(s1, s2);
        when(s3Client.putObject(any(PutObjectRequest.class))).thenReturn(null);
        when(s3Client.generatePresignedUrl(any(), any(), any())).thenReturn(new java.net.URL("http://example.com/test.json"));

        CompletableFuture<String> result = exportService.exportToJson(subscribers);
        String url = result.get();

        assertEquals("http://example.com/test.json", url);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class));
    }
}
