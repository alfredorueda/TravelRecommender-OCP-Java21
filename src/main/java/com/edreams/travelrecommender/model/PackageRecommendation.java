package com.edreams.travelrecommender.model;

import java.util.List;

/**
 * A record representing a package recommendation that combines multiple types of recommendations.
 * 
 * OCP Java 21 Note: This demonstrates using generics with records to create a flexible
 * yet type-safe composite recommendation.
 */
public record PackageRecommendation(
    String id,
    String title,
    String description,
    double confidenceScore,
    FlightRecommendation flight,
    HotelRecommendation hotel,
    List<ActivityRecommendation> activities,
    double packageDiscount,
    double totalPrice
) implements Recommendation {
    
    /**
     * Compact constructor with validation.
     */
    public PackageRecommendation {
        if (confidenceScore < 0.0 || confidenceScore > 1.0) {
            throw new IllegalArgumentException("Confidence score must be between 0.0 and 1.0");
        }
        if (packageDiscount < 0.0 || packageDiscount > 1.0) {
            throw new IllegalArgumentException("Package discount must be between 0.0 and 1.0");
        }
        if (totalPrice < 0.0) {
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
     * Returns the number of days for this package.
     */
    public int getDurationInDays() {
        // For simplicity, assume package duration is based on activities
        return Math.max(3, activities.size() / 2);
    }
    
    /**
     * Returns the savings amount from the package discount.
     */
    public double getSavingsAmount() {
        double individualPrices = flight.price() + 
                                 (hotel.pricePerNight() * getDurationInDays()) +
                                 activities.stream().mapToDouble(ActivityRecommendation::price).sum();
        return individualPrices - totalPrice;
    }
}