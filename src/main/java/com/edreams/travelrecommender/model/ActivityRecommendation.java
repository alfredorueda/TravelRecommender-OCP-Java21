package com.edreams.travelrecommender.model;

import java.time.Duration;
import java.util.List;

/**
 * A record representing an activity or excursion recommendation in the travel system.
 * 
 * <h2>Purpose in Architecture</h2>
 * <p>ActivityRecommendation is one of the four permitted implementations of the
 * {@link Recommendation} sealed interface. It encapsulates information about 
 * tours, experiences, and excursions that travelers might be interested in during
 * their trips, providing details such as activity type, duration, and pricing.</p>
 * 
 * <h2>OCP Java 21 Features</h2>
 * <p>This class demonstrates several key features covered in the OCP Java 21 exam:</p>
 * <ul>
 *   <li><strong>Records</strong>: Provides concise data modeling with automatic implementations
 *       of constructors, accessors, equals(), hashCode(), and toString()</li>
 *   <li><strong>Sealed Types</strong>: Implements a permitted subtype of the sealed
 *       Recommendation interface</li>
 *   <li><strong>Time API</strong>: Utilizes Java's modern time API with the Duration class</li>
 *   <li><strong>Pattern Matching</strong>: Designed to work with pattern matching in switch
 *       expressions</li>
 * </ul>
 * 
 * <h2>Domain Modeling</h2>
 * <p>The record components capture essential information about an activity:</p>
 * <ul>
 *   <li><strong>Common recommendation fields</strong>: id, title, description, confidenceScore</li>
 *   <li><strong>Activity-specific fields</strong>: activityName, location, duration, categories, etc.</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Creating an activity recommendation
 * var activity = new ActivityRecommendation(
 *     "ACT123",
 *     "Guided Museum Tour",
 *     "Expert-led tour of the finest art museum in Paris",
 *     0.85,
 *     "Louvre Museum Tour",
 *     "Paris, France",
 *     Duration.ofHours(3),
 *     List.of("Cultural", "Museum", "Art"),
 *     true,
 *     45.00,
 *     8
 * );
 * 
 * // Using with pattern matching
 * if (recommendation instanceof ActivityRecommendation act) {
 *     System.out.println(act.getActivityType() + " lasting " + 
 *                        act.getFormattedDuration());
 * }
 * }</pre>
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
     * Compact constructor with validation logic for ActivityRecommendation.
     * 
     * <p>This constructor validates the input data before the canonical constructor
     * runs, ensuring all activity recommendations comply with business rules. It
     * validates both common recommendation attributes and activity-specific rules.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> Compact constructors are a feature specific
     * to records that provide a concise way to validate input data. They execute before
     * the implicit canonical constructor that initializes the record components.</p>
     * 
     * <p>The validation ensures:</p>
     * <ul>
     *   <li>Confidence score is within the valid range (0.0 to 1.0)</li>
     *   <li>Price is a non-negative value</li>
     *   <li>Minimum age requirement is a non-negative value</li>
     * </ul>
     * 
     * @throws IllegalArgumentException if any validation constraint is violated
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
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the ID component of the activity recommendation.</p>
     */
    @Override
    public String getId() {
        return id;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the title component of the activity recommendation.</p>
     */
    @Override
    public String getTitle() {
        return title;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the description component of the activity recommendation.</p>
     */
    @Override
    public String getDescription() {
        return description;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the confidenceScore component of the activity recommendation.</p>
     */
    @Override
    public double getConfidenceScore() {
        return confidenceScore;
    }
    
    /**
     * Returns a categorization of the activity based on its attributes.
     * 
     * <p>This method demonstrates how records can include business logic methods
     * that derive values from the record components. It analyzes the categories list
     * and the indoor/outdoor status to determine a meaningful activity type that
     * can be used for filtering or display purposes.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> This method shows how records can include
     * domain logic methods that operate on the immutable record components without
     * changing their state. It's a good example of behavior in an otherwise data-focused
     * component.</p>
     * 
     * <p>The categorization logic:</p>
     * <ul>
     *   <li>Prioritizes adventure activities that are outdoors</li>
     *   <li>Identifies cultural experiences based on specific categories</li>
     *   <li>Identifies culinary experiences</li>
     *   <li>Falls back to simple indoor/outdoor classification</li>
     * </ul>
     * 
     * @return a string describing the type of activity
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
     * Returns a human-friendly formatted string representing the activity duration.
     * 
     * <p>This method takes the Duration component and converts it into a readable
     * string format that is appropriate for display to users. It handles the formatting
     * logic to ensure natural language output.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> This method demonstrates:</p>
     * <ul>
     *   <li>Working with the modern Java Time API (Duration class)</li>
     *   <li>Using String formatting to create human-readable output</li>
     *   <li>Conditional logic to handle different time granularities</li>
     * </ul>
     * 
     * <p>The method formats the duration as:</p>
     * <ul>
     *   <li>"X hours Y minutes" when there are both hours and minutes</li>
     *   <li>"X hours" when there are only hours</li>
     *   <li>"Y minutes" when there are only minutes</li>
     * </ul>
     * 
     * @return a formatted string representing the activity duration
     */
    public String getFormattedDuration() {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        
        if (hours > 0) {
            return String.format("%d hours%s", hours, minutes > 0 ? " " + minutes + " minutes" : "");
        } else {
            return String.format("%d minutes", minutes);
        }
    }
}