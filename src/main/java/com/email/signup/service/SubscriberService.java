package com.email.signup.service;

import com.email.signup.model.Subscriber;
import java.util.List;

/**
 * Service interface for subscriber-related operations.
 * Defines methods for retrieving subscriber data.
 */
public interface SubscriberService {
    /**
     * Retrieves all subscribers from the database.
     * @return List of all subscribers
     */
    List<Subscriber> getAllSubscribers();
}
