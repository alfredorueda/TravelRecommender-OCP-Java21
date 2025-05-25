package com.edreams.travelrecommender.model;

/**
 * A sealed interface representing a travel recommendation.
 * 
 * OCP Java 21 Note: Sealed types allow you to control which classes can implement an interface
 * or extend a class. This is useful for domain modeling and API design where you want to
 * restrict the hierarchy to a known set of subtypes.
 */
public sealed interface Recommendation 
    permits FlightRecommendation, HotelRecommendation, ActivityRecommendation, PackageRecommendation {
    
    /**
     * Returns the unique identifier of the recommendation.
     */
    String getId();
    
    /**
     * Returns the title of the recommendation.
     */
    String getTitle();
    
    /**
     * Returns the description of the recommendation.
     */
    String getDescription();
    
    /**
     * Returns the confidence score for this recommendation (0.0 to 1.0).
     */
    double getConfidenceScore();
}