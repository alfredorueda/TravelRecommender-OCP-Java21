package com.edreams.travelrecommender.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A record representing a flight recommendation in the travel recommendation system.
 * 
 * <h2>Purpose in Architecture</h2>
 * <p>FlightRecommendation serves as one of the four permitted implementations of the
 * {@link Recommendation} sealed interface. It encapsulates all the relevant information
 * for recommending a specific flight to a traveler, focusing on critical details like
 * departure/arrival times, airport locations, and flight characteristics.</p>
 * 
 * <h2>OCP Java 21 Features</h2>
 * <p>This class demonstrates multiple Java 21 features:</p>
 * <ul>
 *   <li><strong>Records</strong>: Provides a concise syntax for creating immutable data carriers
 *       with automatic implementations of equals(), hashCode(), toString(), and accessors</li>
 *   <li><strong>Sealed Types</strong>: Implements a permitted subtype of the sealed Recommendation interface</li>
 *   <li><strong>Pattern Matching</strong>: Designed to work with pattern matching in switch expressions</li>
 * </ul>
 * 
 * <h2>Record Components as Domain Model</h2>
 * <p>The record components directly model the domain concepts of a flight recommendation:</p>
 * <ul>
 *   <li><strong>Core recommendation fields</strong>: id, title, description, confidenceScore</li>
 *   <li><strong>Flight-specific fields</strong>: departureAirport, arrivalAirport, departureTime, etc.</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Creating a flight recommendation
 * var flight = new FlightRecommendation(
 *     "FL123",
 *     "Direct flight to Paris",
 *     "Comfortable direct flight to Paris with Air France",
 *     0.85,
 *     "JFK",
 *     "CDG",
 *     LocalDateTime.parse("2023-10-15T08:30:00"),
 *     LocalDateTime.parse("2023-10-15T20:15:00"),
 *     "Air France",
 *     true,
 *     List.of("Wi-Fi", "Meal included"),
 *     450.99
 * );
 * 
 * // Using with pattern matching
 * if (recommendation instanceof FlightRecommendation flight) {
 *     System.out.println("Flight to " + flight.arrivalAirport() + " with " + flight.airline());
 * }
 * }</pre>
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
     * Compact constructor with validation logic for the FlightRecommendation record.
     * 
     * <p>Record compact constructors allow validation of components before the implicit
     * canonical constructor runs. This ensures that all FlightRecommendation instances
     * have valid data, maintaining the integrity of the domain model.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> Compact constructors:</p>
     * <ul>
     *   <li>Cannot reassign values to record components (unlike regular constructors)</li>
     *   <li>Can validate input values before the implicit canonical constructor runs</li>
     *   <li>Can transform input values for use in the canonical constructor</li>
     *   <li>Cannot use explicit constructor parameters (they're implicit)</li>
     * </ul>
     * 
     * <p>In this example, we validate that:</p>
     * <ul>
     *   <li>Confidence score is within valid range (0.0 to 1.0)</li>
     *   <li>Price is not negative</li>
     * </ul>
     * 
     * @throws IllegalArgumentException if validation fails
     */
    public FlightRecommendation {
        if (confidenceScore < 0.0 || confidenceScore > 1.0) {
            throw new IllegalArgumentException("Confidence score must be between 0.0 and 1.0");
        }
        if (price < 0.0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the ID component of the flight recommendation.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> In records, it's common to explicitly implement
     * interface methods even when they simply return a record component. This makes the
     * code more readable and clearly shows the mapping between interface methods and
     * record components.</p>
     */
    @Override
    public String getId() {
        return id;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the title component of the flight recommendation.</p>
     */
    @Override
    public String getTitle() {
        return title;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the description component of the flight recommendation.</p>
     */
    @Override
    public String getDescription() {
        return description;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the confidenceScore component of the flight recommendation.</p>
     */
    @Override
    public double getConfidenceScore() {
        return confidenceScore;
    }
    
    /**
     * Returns a formatted string representing the flight duration.
     * 
     * <p>This method demonstrates how records can include additional derived methods
     * that calculate values based on the record components. It calculates the duration
     * between departure and arrival times and formats it as a human-readable string.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> Records can contain methods beyond accessors,
     * allowing them to provide additional functionality while maintaining immutability.</p>
     * 
     * @return a string representing the flight duration in hours and minutes format
     */
    public String getDuration() {
        var duration = java.time.Duration.between(departureTime, arrivalTime);
        return String.format("%d hours, %d minutes", 
            duration.toHours(), 
            duration.toMinutesPart());
    }
}