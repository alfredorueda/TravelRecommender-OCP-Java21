package com.edreams.travelrecommender.model;

/**
 * A sealed interface representing a travel recommendation in the eDreams-style travel system.
 * 
 * <h2>Purpose in Architecture</h2>
 * <p>This interface forms the foundation of the type hierarchy for all recommendations
 * in the travel system. It defines the common contract that all recommendation types
 * must fulfill, while using the sealed modifier to strictly control which implementations
 * are permitted.</p>
 * 
 * <h2>OCP Java 21 Feature: Sealed Types</h2>
 * <p>Sealed types (introduced in Java 17, refined in Java 21) allow precise control over
 * class hierarchies by explicitly specifying which classes can implement an interface or 
 * extend a class. This enables:</p>
 * <ul>
 *   <li><strong>Closed polymorphism</strong>: The compiler knows all possible subtypes</li>
 *   <li><strong>Exhaustive pattern matching</strong>: Switch expressions can safely cover all cases</li>
 *   <li><strong>Better domain modeling</strong>: The class hierarchy accurately reflects the domain constraints</li>
 * </ul>
 * 
 * <p>In this example, only four record types are permitted to implement this interface:</p>
 * <ul>
 *   <li>{@link FlightRecommendation}: For flight booking recommendations</li>
 *   <li>{@link HotelRecommendation}: For hotel stay recommendations</li>
 *   <li>{@link ActivityRecommendation}: For activity and excursion recommendations</li>
 *   <li>{@link PackageRecommendation}: For bundled travel packages combining the above</li>
 * </ul>
 * 
 * <h2>Usage with Pattern Matching</h2>
 * <p>The sealed nature of this interface enables exhaustive pattern matching in switch expressions:</p>
 * <pre>{@code
 * String describe(Recommendation rec) {
 *     return switch(rec) {
 *         case FlightRecommendation flight -> "Flight to " + flight.arrivalAirport();
 *         case HotelRecommendation hotel -> hotel.starRating() + "-star hotel";
 *         case ActivityRecommendation activity -> "Activity: " + activity.activityName();
 *         case PackageRecommendation pkg -> "Package with " + pkg.activities().size() + " activities";
 *         // No default needed - compiler knows these are all possibilities
 *     };
 * }
 * }</pre>
 * 
 * <h2>OCP Java 21 Exam Note</h2>
 * <p>For the OCP exam, understand that:</p>
 * <ul>
 *   <li>All permitted subclasses must be in the same module as the sealed type</li>
 *   <li>All permitted subclasses must extend/implement the sealed type</li>
 *   <li>All permitted subclasses must use a modifier: {@code final}, {@code sealed}, or {@code non-sealed}</li>
 * </ul>
 */
public sealed interface Recommendation 
    permits FlightRecommendation, HotelRecommendation, ActivityRecommendation, PackageRecommendation {
    
    /**
     * Returns the unique identifier of the recommendation.
     * 
     * <p>This identifier should be consistent across system restarts and can be used
     * for database persistence, caching, and correlation between different services.</p>
     * 
     * @return a string containing the unique identifier
     */
    String getId();
    
    /**
     * Returns the user-friendly title of the recommendation.
     * 
     * <p>This title should be suitable for display in user interfaces and should
     * concisely summarize the recommendation in a way that is meaningful to travelers.</p>
     * 
     * <p><strong>Examples:</strong></p>
     * <ul>
     *   <li>For flights: "Direct flight to Paris with Air France"</li>
     *   <li>For hotels: "Luxury stay at Marriott with sea view"</li>
     * </ul>
     * 
     * @return a string containing the recommendation title
     */
    String getTitle();
    
    /**
     * Returns the detailed description of the recommendation.
     * 
     * <p>This description provides comprehensive information about the recommendation,
     * elaborating on features, benefits, and any other details that would help a
     * traveler make an informed decision.</p>
     * 
     * @return a string containing the detailed description
     */
    String getDescription();
    
    /**
     * Returns the confidence score for this recommendation.
     * 
     * <p>The confidence score indicates how well this recommendation matches the user's
     * preferences or requirements. Higher values indicate stronger matches.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> This method demonstrates a behavioral
     * contract enforced by the interface across all implementations, which is useful
     * for generic algorithms that need to work with any recommendation type.</p>
     * 
     * @return a double value between 0.0 (lowest confidence) and 1.0 (highest confidence)
     */
    double getConfidenceScore();
}