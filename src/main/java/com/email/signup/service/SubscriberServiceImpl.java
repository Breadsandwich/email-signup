package com.email.signup.service;

import com.email.signup.model.Subscriber;
import com.email.signup.repository.SubscriberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of SubscriberService that handles subscriber data operations.
 * Uses JPA repository for database interactions.
 */
@Service
public class SubscriberServiceImpl implements SubscriberService {

    private final SubscriberRepository subscriberRepository;

    public SubscriberServiceImpl(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    /**
     * Fetches all subscribers from the database using JPA repository.
     * @return Complete list of subscribers
     */
    @Override
    public List<Subscriber> getAllSubscribers() {
        return subscriberRepository.findAll();
    }
}
