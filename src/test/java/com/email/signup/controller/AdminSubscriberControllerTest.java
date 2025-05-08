package com.email.signup.controller;

import com.email.signup.model.Subscriber;
import com.email.signup.repository.SubscriberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminSubscriberController.class)
class AdminSubscriberControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriberRepository subscriberRepository;

    private Subscriber testSubscriber;

    @BeforeEach
    void setUp() {
        testSubscriber = new Subscriber("danielthai6570@gmail.com", "daniel123");
        testSubscriber.setVerified(true);
        testSubscriber.setConfirmationToken("token123");
        testSubscriber.setUnsubscribedAt(null);
    }

    @Test
    void listAllSubscribers() throws Exception {
        Mockito.when(subscriberRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testSubscriber)));

        mockMvc.perform(get("/admin/subscribers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("danielthai6570@gmail.com"));
    }

    @Test
    void filterByEmail() throws Exception {
        Mockito.when(subscriberRepository.findByEmail(eq("danielthai6570@gmail.com")))
                .thenReturn(Optional.of(testSubscriber));

        mockMvc.perform(get("/admin/subscribers?email=danielthai6570@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("danielthai6570@gmail.com"));
    }

    @Test
    void filterByVerified() throws Exception {
        Mockito.when(subscriberRepository.findByVerified(eq(true), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(testSubscriber)));

        mockMvc.perform(get("/admin/subscribers?verified=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].verified").value(true));
    }

    @Test
    void deleteSubscriber_success() throws Exception {
        java.util.UUID id = java.util.UUID.randomUUID();
        Mockito.when(subscriberRepository.findById(id)).thenReturn(java.util.Optional.of(testSubscriber));

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/subscribers/" + id))
                .andExpect(status().isNoContent());
        Mockito.verify(subscriberRepository).delete(testSubscriber);
    }

    @Test
    void deleteSubscriber_notFound() throws Exception {
        java.util.UUID id = java.util.UUID.randomUUID();
        Mockito.when(subscriberRepository.findById(id)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/subscribers/" + id))
                .andExpect(status().isNotFound());
    }
}
