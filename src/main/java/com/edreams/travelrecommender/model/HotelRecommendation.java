package com.edreams.travelrecommender.model;

import java.util.List;

/**
 * A record representing a hotel recommendation.
 * 
 * OCP Java 21 Note: As a permitted subtype of a sealed interface,
 * this record must implement the Recommendation interface.
 */
public record HotelRecommendation(
    String id,
    String title,
    String description,
    double confidenceScore,
    String hotelName,
    int starRating,
    String location,
    List<String> amenities,
    double pricePerNight,
    double distanceToCenter,
    boolean hasFreeCancellation
) implements Recommendation {
    
    /**
     * Compact constructor with validation.
     */
    public HotelRecommendation {
        if (confidenceScore < 0.0 || confidenceScore > 1.0) {
            throw new IllegalArgumentException("Confidence score must be between 0.0 and 1.0");
        }
        if (starRating < 1 || starRating > 5) {
            throw new IllegalArgumentException("Star rating must be between 1 and 5");
        }
        if (pricePerNight < 0.0) {
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
     * Returns the type of hotel based on star rating.
     */
    public String getHotelType() {
        return switch (starRating) {
            case 1, 2 -> "Budget";
            case 3 -> "Standard";
            case 4 -> "Premium";
            case 5 -> "Luxury";
            default -> throw new IllegalStateException("Invalid star rating: " + starRating);
        };
    }
}