package com.edreams.travelrecommender.model;

import java.time.Duration;
import java.util.List;

/**
 * A record representing an activity recommendation.
 * 
 * OCP Java 21 Note: Records automatically provide equals(), hashCode(), 
 * toString(), and accessor methods for each component.
 */
public record ActivityRecommendation(
    String id,
    String title,
    String description,
    double confidenceScore,
    String activityName,
    String location,
    Duration duration,
    List<String> categories,
    boolean isIndoor,
    double price,
    int minimumAge
) implements Recommendation {
    
    /**
     * Compact constructor with validation.
     */
    public ActivityRecommendation {
        if (confidenceScore < 0.0 || confidenceScore > 1.0) {
            throw new IllegalArgumentException("Confidence score must be between 0.0 and 1.0");
        }
        if (price < 0.0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (minimumAge < 0) {
            throw new IllegalArgumentException("Minimum age cannot be negative");
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
     * Returns the type of activity based on whether it's indoor or outdoor and its categories.
     */
    public String getActivityType() {
        if (categories.contains("Adventure") && !isIndoor) {
            return "Outdoor Adventure";
        } else if (categories.contains("Cultural") || categories.contains("Museum")) {
            return "Cultural Experience";
        } else if (categories.contains("Food") || categories.contains("Dining")) {
            return "Culinary Experience";
        } else {
            return isIndoor ? "Indoor Activity" : "Outdoor Activity";
        }
    }
    
    /**
     * Returns a formatted string with activity duration.
     */
    public String getFormattedDuration() {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        
        if (hours > 0) {
            return String.format("%d hours %s", hours, minutes > 0 ? minutes + " minutes" : "");
        } else {
            return String.format("%d minutes", minutes);
        }
    }
}