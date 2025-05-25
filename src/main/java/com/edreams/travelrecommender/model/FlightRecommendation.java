package com.edreams.travelrecommender.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A record representing a flight recommendation.
 * 
 * OCP Java 21 Note: Records provide a concise way to create immutable data carriers.
 * When combined with sealed interfaces, they create powerful, type-safe domain models.
 */
public record FlightRecommendation(
    String id,
    String title,
    String description,
    double confidenceScore,
    String departureAirport,
    String arrivalAirport,
    LocalDateTime departureTime,
    LocalDateTime arrivalTime,
    String airline,
    boolean isDirect,
    List<String> amenities,
    double price
) implements Recommendation {
    
    /**
     * Compact constructor with validation.
     * 
     * OCP Java 21 Note: Records can have compact constructors that validate input 
     * but cannot change the values of the record components.
     */
    public FlightRecommendation {
        if (confidenceScore < 0.0 || confidenceScore > 1.0) {
            throw new IllegalArgumentException("Confidence score must be between 0.0 and 1.0");
        }
        if (price < 0.0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public double getConfidenceScore() {
        return confidenceScore;
    }
    
    /**
     * Returns a formatted string with flight duration.
     */
    public String getDuration() {
        var duration = java.time.Duration.between(departureTime, arrivalTime);
        return String.format("%d hours, %d minutes", 
            duration.toHours(), 
            duration.toMinutesPart());
    }
}