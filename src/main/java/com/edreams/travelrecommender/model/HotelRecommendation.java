package com.edreams.travelrecommender.model;

import java.util.List;

/**
 * A record representing a hotel accommodation recommendation in the travel system.
 * 
 * <h2>Purpose in Architecture</h2>
 * <p>HotelRecommendation is one of the four permitted implementations of the
 * {@link Recommendation} sealed interface. It models accommodation recommendations
 * with hotel-specific attributes such as star rating, amenities, and location details
 * that are essential for travelers making lodging decisions.</p>
 * 
 * <h2>OCP Java 21 Features</h2>
 * <p>This class demonstrates several key Java features relevant to the OCP Java 21 exam:</p>
 * <ul>
 *   <li><strong>Records</strong>: Uses Java's record feature for concise, immutable data modeling</li>
 *   <li><strong>Sealed Types</strong>: Implements a permitted subtype of the sealed Recommendation interface</li>
 *   <li><strong>Pattern Matching</strong>: Compatible with pattern matching in switch expressions</li>
 *   <li><strong>Switch Expressions</strong>: Uses the modern switch expression syntax in getHotelType()</li>
 * </ul>
 * 
 * <h2>Domain Modeling</h2>
 * <p>The record components model key attributes of hotel recommendations:</p>
 * <ul>
 *   <li><strong>Base recommendation fields</strong>: id, title, description, confidenceScore</li>
 *   <li><strong>Hotel-specific fields</strong>: hotelName, starRating, location, amenities, etc.</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Creating a hotel recommendation
 * var hotel = new HotelRecommendation(
 *     "HT001",
 *     "Luxury Downtown Hotel",
 *     "5-star luxury hotel in the heart of the city",
 *     0.92,
 *     "Grand Hyatt",
 *     5,
 *     "Downtown",
 *     List.of("Spa", "Pool", "Free WiFi", "Restaurant"),
 *     250.00,
 *     0.5,
 *     true
 * );
 * 
 * // Using with pattern matching in a processing method
 * void processRecommendation(Recommendation rec) {
 *     if (rec instanceof HotelRecommendation hotel) {
 *         System.out.println(hotel.getHotelType() + " hotel: " + hotel.hotelName());
 *     }
 * }
 * }</pre>
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
     * Compact constructor with validation logic for HotelRecommendation.
     * 
     * <p>This constructor validates the input values before the canonical constructor
     * executes, ensuring the creation of valid hotel recommendation instances. The validation
     * focuses on business rules specific to hotel recommendations.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> Compact constructors are a feature of records
     * that allow for input validation without duplicating parameter declarations. They
     * execute before the implicit canonical constructor that initializes the record fields.</p>
     * 
     * <p>The validation ensures:</p>
     * <ul>
     *   <li>Confidence score is within the valid range (0.0 to 1.0)</li>
     *   <li>Star rating follows industry standards (1 to 5 stars)</li>
     *   <li>Price per night is a non-negative value</li>
     * </ul>
     * 
     * @throws IllegalArgumentException if any validation constraint is violated
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
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the ID component of the hotel recommendation.</p>
     */
    @Override
    public String getId() {
        return id;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the title component of the hotel recommendation.</p>
     */
    @Override
    public String getTitle() {
        return title;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the description component of the hotel recommendation.</p>
     */
    @Override
    public String getDescription() {
        return description;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the confidenceScore component of the hotel recommendation.</p>
     */
    @Override
    public double getConfidenceScore() {
        return confidenceScore;
    }
    
    /**
     * Returns the hotel category type based on its star rating.
     * 
     * <p>This method translates the numeric star rating into a meaningful category label
     * that travelers can easily understand. It uses a switch expression to map star ratings
     * to hotel categories.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> This method demonstrates the modern switch
     * expression syntax introduced in Java 14 and refined in later versions. Key features include:</p>
     * <ul>
     *   <li>Arrow syntax (-&gt;) for concise case handling</li>
     *   <li>Multiple case labels in a single case (case 1, 2)</li>
     *   <li>Expression-oriented syntax that yields a value</li>
     *   <li>Exhaustive case handling (all possible star ratings are covered)</li>
     * </ul>
     * 
     * <p>The default case handles invalid states, though due to constructor validation,
     * it should never be reached in practice.</p>
     * 
     * @return a string representing the hotel category (Budget, Standard, Premium, or Luxury)
     * @throws IllegalStateException if the star rating is outside the valid range (should not
     *         happen due to constructor validation)
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