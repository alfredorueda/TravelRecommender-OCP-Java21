package com.edreams.travelrecommender.model;

import java.util.List;

/**
 * A record representing a package recommendation that combines multiple travel components.
 * 
 * <h2>Purpose in Architecture</h2>
 * <p>PackageRecommendation is one of the four permitted implementations of the
 * {@link Recommendation} sealed interface. It serves as a composite recommendation
 * that bundles multiple travel components (flight, hotel, and activities) into a
 * single cohesive offering with potential discounts and simplified booking.</p>
 * 
 * <h2>OCP Java 21 Features</h2>
 * <p>This class demonstrates several advanced Java features relevant to the OCP exam:</p>
 * <ul>
 *   <li><strong>Records</strong>: Uses Java's record feature for immutable data modeling</li>
 *   <li><strong>Sealed Types</strong>: Implements a permitted subtype of the sealed Recommendation interface</li>
 *   <li><strong>Composition</strong>: Demonstrates object composition by aggregating other recommendation types</li>
 *   <li><strong>Functional Programming</strong>: Uses streams to process collections in the getSavingsAmount() method</li>
 * </ul>
 * 
 * <h2>Design Pattern</h2>
 * <p>This record implements a form of the Composite pattern, where a PackageRecommendation
 * can contain other recommendations. This creates a part-whole hierarchy that allows
 * clients to treat individual recommendations and compositions of recommendations uniformly.</p>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create individual recommendations
 * FlightRecommendation flight = new FlightRecommendation(...);
 * HotelRecommendation hotel = new HotelRecommendation(...);
 * List<ActivityRecommendation> activities = List.of(
 *     new ActivityRecommendation(...),
 *     new ActivityRecommendation(...)
 * );
 * 
 * // Create a bundled package with a discount
 * PackageRecommendation package = new PackageRecommendation(
 *     "PKG001",
 *     "Weekend in Paris",
 *     "Romantic weekend getaway including flight, hotel and activities",
 *     0.85,
 *     flight,
 *     hotel,
 *     activities,
 *     0.15,  // 15% discount
 *     850.00
 * );
 * 
 * // Access package details
 * System.out.println("Package saves you: $" + package.getSavingsAmount());
 * }</pre>
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
     * Compact constructor with validation logic for PackageRecommendation.
     * 
     * <p>This constructor validates the input values before the canonical constructor
     * runs, ensuring that all package recommendations have valid business data. The validation
     * focuses on the numeric values that have specific valid ranges.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> Compact constructors are a feature specific
     * to records that validate or normalize data without explicitly listing the parameters.
     * This reduces boilerplate code while maintaining data integrity.</p>
     * 
     * <p>The validation ensures:</p>
     * <ul>
     *   <li>Confidence score is within the valid range (0.0 to 1.0)</li>
     *   <li>Package discount is within the valid range (0.0 to 1.0)</li>
     *   <li>Total price is a non-negative value</li>
     * </ul>
     * 
     * <p>Note that individual components (flight, hotel, activities) are assumed to be
     * already validated through their own constructors.</p>
     * 
     * @throws IllegalArgumentException if any validation constraint is violated
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
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the ID component of the package recommendation.</p>
     */
    @Override
    public String getId() {
        return id;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the title component of the package recommendation.</p>
     */
    @Override
    public String getTitle() {
        return title;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the description component of the package recommendation.</p>
     */
    @Override
    public String getDescription() {
        return description;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>This implementation returns the confidenceScore component of the package recommendation.</p>
     */
    @Override
    public double getConfidenceScore() {
        return confidenceScore;
    }
    
    /**
     * Returns the estimated duration of the package in days.
     * 
     * <p>This method calculates the recommended duration for the travel package
     * based on the number of included activities. It demonstrates how records can
     * include methods that derive useful information from their components.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> Records can contain additional methods
     * beyond the automatically generated accessors. These methods can provide
     * calculated values based on the record components.</p>
     * 
     * <p>The calculation ensures:</p>
     * <ul>
     *   <li>A minimum duration of 3 days for any package</li>
     *   <li>Longer durations for packages with many activities (1 day per 2 activities)</li>
     * </ul>
     * 
     * @return the recommended duration of the package in days
     */
    public int getDurationInDays() {
        // For simplicity, assume package duration is based on activities
        return Math.max(3, activities.size() / 2);
    }
    
    /**
     * Calculates the monetary savings provided by this package compared to booking each component separately.
     * 
     * <p>This method demonstrates how records can include complex business logic that
     * operates on their components. It calculates the difference between the sum of
     * individual prices and the discounted package price.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> This method showcases:</p>
     * <ul>
     *   <li>Functional programming with streams to calculate the sum of activity prices</li>
     *   <li>Method references (ActivityRecommendation::price) for concise code</li>
     *   <li>Complex calculations based on multiple record components</li>
     * </ul>
     * 
     * <p>The calculation:</p>
     * <ol>
     *   <li>Sums the flight price</li>
     *   <li>Adds the hotel price multiplied by the duration</li>
     *   <li>Adds all activity prices</li>
     *   <li>Subtracts the package total price from this sum</li>
     * </ol>
     * 
     * @return the amount saved by booking the package instead of individual components
     */
    public double getSavingsAmount() {
        double individualPrices = flight.price() + 
                                 (hotel.pricePerNight() * getDurationInDays()) +
                                 activities.stream().mapToDouble(ActivityRecommendation::price).sum();
        return individualPrices - totalPrice;
    }
}