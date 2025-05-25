package com.edreams.travelrecommender;

import com.edreams.travelrecommender.model.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for processing and managing travel recommendations in the eDreams-style system.
 * 
 * <h2>Purpose in Architecture</h2>
 * <p>The RecommendationService class serves as the central processing unit for recommendation
 * operations in the travel system. It provides utility methods for filtering, combining,
 * categorizing, and describing recommendations of various types. This class demonstrates
 * how to work with generic collections of sealed hierarchy types in a type-safe manner.</p>
 * 
 * <h2>OCP Java 21 Features</h2>
 * <p>This class is rich with examples of advanced Java features that appear on the OCP Java 21 exam:</p>
 * <ul>
 *   <li><strong>Generic Methods</strong>: Methods that operate on different recommendation types</li>
 *   <li><strong>Bounded Type Parameters</strong>: Using {@code <T extends Recommendation>} to constrain types</li>
 *   <li><strong>Wildcards</strong>: Using {@code ? extends T} and {@code ? super T} following PECS</li>
 *   <li><strong>Pattern Matching</strong>: Both for instanceof and switch expressions</li>
 *   <li><strong>Functional Programming</strong>: With predicates and method references</li>
 *   <li><strong>Type Erasure</strong>: Demonstrating the limitations of Java's generic type system</li>
 *   <li><strong>List.getFirst()</strong>: Using the new Java 21 List API method to access the first element</li>
 * </ul>
 * 
 * <h2>PECS Principle</h2>
 * <p>This class provides an excellent example of the PECS principle (Producer Extends, Consumer Super):</p>
 * <ul>
 *   <li><strong>Producer methods</strong> like {@code filterByPredicate} use {@code ? extends T} when
 *       reading from collections (collections produce values)</li>
 *   <li><strong>Consumer methods</strong> like {@code addRecommendation} use {@code ? super T} when
 *       adding to collections (collections consume values)</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create lists of specific recommendation types
 * List<FlightRecommendation> flights = getFlights();
 * List<HotelRecommendation> hotels = getHotels();
 * 
 * // Filter high-confidence recommendations
 * List<FlightRecommendation> bestFlights = 
 *     RecommendationService.filterByConfidence(flights, 0.8);
 * 
 * // Combine different recommendation types into a common list
 * List<Recommendation> allRecommendations = 
 *     RecommendationService.<Recommendation>combineRecommendations(flights, hotels);
 * 
 * // Describe a recommendation using pattern matching
 * String description = RecommendationService.describeRecommendation(flights.getFirst());
 * }</pre>
 */
public class RecommendationService {
    /**
     * Filters a list of recommendations using a custom predicate condition.
     * 
     * <p>This is a core utility method that demonstrates both bounded type parameters
     * and wildcard usage to achieve maximum flexibility with type safety. It can
     * filter any type of recommendation using any predicate that accepts that type
     * or a supertype.</p>
     * 
     * <h3>OCP Java 21 Feature: Bounded Wildcards</h3>
     * <p>This method demonstrates the "Producer Extends" part of the PECS principle:</p>
     * <ul>
     *   <li>{@code List<? extends T>} indicates that the list produces T instances</li>
     *   <li>{@code Predicate<? super T>} indicates that the predicate consumes T instances</li>
     * </ul>
     * 
     * <p>This combination allows maximum flexibility:</p>
     * <pre>{@code
     * // Example: Filter flights by a Recommendation-level predicate
     * List<FlightRecommendation> directFlights = filterByPredicate(
     *     flights,
     *     flight -> flight.isDirect() && flight.getConfidenceScore() > 0.7
     * );
     * 
     * // Example: Filter any recommendation using a general predicate
     * Predicate<Recommendation> highConfidence = rec -> rec.getConfidenceScore() > 0.9;
     * List<HotelRecommendation> luxuryHotels = filterByPredicate(hotels, highConfidence);
     * }</pre>
     * 
     * @param <T> the type parameter for recommendations, bounded by the Recommendation interface
     * @param recommendations the source list of recommendations to filter (producer of T)
     * @param predicate the condition to test each recommendation against (consumer of T)
     * @return a new list containing only the recommendations that satisfy the predicate
     */
    public static <T extends Recommendation> List<T> filterByPredicate(
            List<? extends T> recommendations, 
            Predicate<? super T> predicate) {
        return recommendations.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    /**
     * Filters recommendations by a minimum confidence score threshold.
     * 
     * <p>This method provides a specialized filtering operation built on top of the more
     * general {@link #filterByPredicate} method. It creates a predicate that checks if
     * a recommendation's confidence score meets or exceeds the specified threshold.</p>
     * 
     * <h3>OCP Java 21 Feature: Lambda Expressions and Method Composition</h3>
     * <p>This method demonstrates:</p>
     * <ul>
     *   <li>How to build specialized methods on top of more general ones</li>
     *   <li>Creating a lambda expression that captures a local variable (minimumConfidence)</li>
     *   <li>Method reuse through composition rather than duplication</li>
     * </ul>
     * 
     * <p>Usage example:</p>
     * <pre>{@code
     * // Get only highly confident hotel recommendations
     * List<HotelRecommendation> bestHotels = 
     *     RecommendationService.filterByConfidence(hotels, 0.85);
     * 
     * // Get all recommendations above a threshold
     * List<Recommendation> goodRecommendations = 
     *     RecommendationService.filterByConfidence(allRecommendations, 0.7);
     * }</pre>
     * 
     * @param <T> the type parameter for recommendations, bounded by the Recommendation interface
     * @param recommendations the source list of recommendations to filter
     * @param minimumConfidence the minimum confidence score (0.0 to 1.0) for inclusion
     * @return a new list containing only recommendations with confidence scores at or above the threshold
     */
    public static <T extends Recommendation> List<T> filterByConfidence(
            List<? extends T> recommendations, 
            double minimumConfidence) {
        return filterByPredicate(recommendations, 
                rec -> rec.getConfidenceScore() >= minimumConfidence);
    }
    
    /**
     * Combines multiple lists of recommendations into a single consolidated list.
     * 
     * <p>This method allows for combining any number of recommendation lists, even if
     * they contain different specific recommendation types, as long as there is a common
     * supertype T that all elements conform to.</p>
     * 
     * <h3>OCP Java 21 Features: Varargs with Generics and Type Erasure</h3>
     * <p>This method demonstrates several advanced concepts:</p>
     * <ul>
     *   <li><strong>Varargs with generics</strong>: Using {@code List<? extends T>...} to accept
     *       any number of lists</li>
     *   <li><strong>@SafeVarargs</strong>: Suppressing unchecked warnings related to varargs</li>
     *   <li><strong>Type erasure</strong>: At runtime, all generic type information is erased</li>
     * </ul>
     * 
     * <h3>Type Erasure Example</h3>
     * <p>During compilation, the generic type information ensures type safety:</p>
     * <pre>{@code
     * // The compiler enforces type compatibility
     * List<FlightRecommendation> flights = getFlights();
     * List<HotelRecommendation> hotels = getHotels();
     * 
     * // Must specify Recommendation as the common supertype
     * List<Recommendation> combined = 
     *     RecommendationService.<Recommendation>combineRecommendations(flights, hotels);
     * }</pre>
     * 
     * <p>But at runtime, due to type erasure, all generic type information is erased:</p>
     * <ul>
     *   <li>{@code List<FlightRecommendation>} becomes just {@code List}</li>
     *   <li>{@code List<? extends T>} becomes just {@code List}</li>
     *   <li>The method actually processes {@code List} objects containing {@code Object} references</li>
     * </ul>
     * 
     * @param <T> the common supertype of all recommendations in the input lists
     * @param lists any number of recommendation lists to combine
     * @return a new list containing all recommendations from all input lists
     */
    @SafeVarargs
    public static <T extends Recommendation> List<T> combineRecommendations(List<? extends T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }
    
    /**
     * Safely adds a recommendation to a list, demonstrating the "Consumer Super" pattern.
     * 
     * <p>This method follows the "Consumer Super" part of the PECS principle, allowing
     * a recommendation to be added to any list that can contain it or its supertypes.</p>
     * 
     * <h3>OCP Java 21 Feature: Super Wildcards</h3>
     * <p>The {@code ? super T} wildcard allows for maximum flexibility in the target list:</p>
     * <ul>
     *   <li>A {@code FlightRecommendation} can be added to {@code List<FlightRecommendation>}</li>
     *   <li>A {@code FlightRecommendation} can be added to {@code List<Recommendation>}</li>
     *   <li>A {@code FlightRecommendation} can be added to {@code List<Object>}</li>
     * </ul>
     * 
     * <p>This follows from the principle of substitutability: if T is a subtype of S,
     * then instances of T can be used wherever instances of S are expected.</p>
     * 
     * <p>Usage example:</p>
     * <pre>{@code
     * // Create lists of different levels in the type hierarchy
     * List<FlightRecommendation> flights = new ArrayList<>();
     * List<Recommendation> recommendations = new ArrayList<>();
     * List<Object> objects = new ArrayList<>();
     * 
     * // Create a flight recommendation
     * FlightRecommendation flight = new FlightRecommendation(...);
     * 
     * // Can add a flight to any of these lists
     * RecommendationService.addRecommendation(flights, flight);       // List<FlightRecommendation>
     * RecommendationService.addRecommendation(recommendations, flight); // List<Recommendation>
     * RecommendationService.addRecommendation(objects, flight);        // List<Object>
     * }</pre>
     * 
     * @param <T> the type of recommendation being added
     * @param recommendations the target list that can hold T or any supertype of T
     * @param recommendation the recommendation instance to add to the list
     */
    public static <T extends Recommendation> void addRecommendation(
            List<? super T> recommendations, 
            T recommendation) {
        recommendations.add(recommendation);
    }
    
    /**
     * Returns a human-readable description of a recommendation based on its specific type.
     * 
     * <p>This method uses pattern matching with switch expressions to handle different
     * recommendation types differently, extracting and formatting type-specific information
     * without explicit casting.</p>
     * 
     * <h3>OCP Java 21 Feature: Pattern Matching for Switch</h3>
     * <p>This demonstrates advanced pattern matching with switch expressions:</p>
     * <ul>
     *   <li><strong>Record patterns</strong>: Using {@code case FlightRecommendation flight ->}
     *       to simultaneously check the type and bind the value to a variable</li>
     *   <li><strong>Arrow syntax</strong>: Using {@code ->} for concise case handling</li>
     *   <li><strong>Expression-oriented</strong>: The switch returns a value</li>
     *   <li><strong>Exhaustiveness checking</strong>: The compiler verifies that all possible
     *       subtypes of the sealed Recommendation interface are handled</li>
     * </ul>
     * 
     * <p>This pattern matching approach eliminates the need for traditional instanceof checks
     * followed by casting, making the code more concise and less error-prone.</p>
     * 
     * <p>Note: Since Recommendation is a sealed interface with exactly four permitted
     * implementations, the compiler can verify that all cases are covered. The default
     * case is technically unreachable but included for robustness.</p>
     * 
     * @param recommendation the recommendation to describe
     * @return a formatted string description tailored to the specific recommendation type
     */
    public static String describeRecommendation(Recommendation recommendation) {
        // Pattern matching for switch with record patterns
        return switch (recommendation) {
            case FlightRecommendation flight -> 
                String.format("Flight from %s to %s on %s, %s, Price: $%.2f", 
                    flight.departureAirport(), 
                    flight.arrivalAirport(), 
                    flight.airline(),
                    flight.isDirect() ? "Direct" : "Connecting",
                    flight.price());
                
            case HotelRecommendation hotel -> 
                String.format("%s-star hotel '%s' in %s, Price: $%.2f per night", 
                    hotel.starRating(), 
                    hotel.hotelName(), 
                    hotel.location(),
                    hotel.pricePerNight());
                
            case ActivityRecommendation activity -> 
                String.format("Activity: %s in %s, Duration: %s, Price: $%.2f", 
                    activity.activityName(), 
                    activity.location(),
                    activity.getFormattedDuration(),
                    activity.price());
                
            case PackageRecommendation pkg -> 
                String.format("Package: %s - Including flight, %d-star hotel, and %d activities, Total price: $%.2f (Save: $%.2f)", 
                    pkg.title(),
                    pkg.hotel().starRating(),
                    pkg.activities().size(),
                    pkg.totalPrice(),
                    pkg.getSavingsAmount());
                
            // Since Recommendation is sealed, this default case will never be reached
            // but it's required for exhaustive pattern matching
            default -> "Unknown recommendation type";
        };
    }
    
    /**
     * Categorizes a mixed list of recommendations into separate collections by their specific types.
     * 
     * <p>This method demonstrates pattern matching with instanceof to sort recommendations
     * into separate lists based on their concrete types. It creates a map where each key
     * corresponds to a recommendation category, and the value is a list of recommendations
     * of that specific type.</p>
     * 
     * <h3>OCP Java 21 Feature: Pattern Matching for instanceof</h3>
     * <p>This method showcases instanceof pattern matching, which:</p>
     * <ul>
     *   <li>Tests the type of an object</li>
     *   <li>Casts it to that type</li>
     *   <li>Assigns it to a variable</li>
     *   <li>All in a single operation</li>
     * </ul>
     * 
     * <p>Compare the modern pattern matching approach:</p>
     * <pre>{@code
     * if (rec instanceof FlightRecommendation flight) {
     *     // Use flight directly without casting
     * }
     * }</pre>
     * 
     * <p>With the traditional approach:</p>
     * <pre>{@code
     * if (rec instanceof FlightRecommendation) {
     *     FlightRecommendation flight = (FlightRecommendation) rec;
     *     // Use flight after explicit casting
     * }
     * }</pre>
     * 
     * <p>The modern approach is more concise and less error-prone.</p>
     * 
     * @param recommendations a list of mixed recommendation types
     * @return a map grouping recommendations by their type category
     */
    public static Map<String, List<Recommendation>> categorizeRecommendations(
            List<? extends Recommendation> recommendations) {
        
        Map<String, List<Recommendation>> categorized = new HashMap<>();
        categorized.put("Flights", new ArrayList<>());
        categorized.put("Hotels", new ArrayList<>());
        categorized.put("Activities", new ArrayList<>());
        categorized.put("Packages", new ArrayList<>());
        
        for (Recommendation rec : recommendations) {
            // Pattern matching with instanceof
            if (rec instanceof FlightRecommendation flight) {
                categorized.get("Flights").add(flight);
            } else if (rec instanceof HotelRecommendation hotel) {
                categorized.get("Hotels").add(hotel);
            } else if (rec instanceof ActivityRecommendation activity) {
                categorized.get("Activities").add(activity);
            } else if (rec instanceof PackageRecommendation pkg) {
                categorized.get("Packages").add(pkg);
            }
        }
        
        return categorized;
    }
    
    /**
     * Demonstrates a key limitation of Java's generic type system: type erasure.
     * 
     * <p>This method highlights one of the most important concepts to understand for
     * the OCP Java 21 exam: type erasure. Due to erasure, generic type information is
     * not available at runtime, which limits what type checks can be performed.</p>
     * 
     * <h3>OCP Java 21 Feature: Type Erasure Limitations</h3>
     * <p>This method demonstrates:</p>
     * <ul>
     *   <li>We <strong>can</strong> check if an object is a List ({@code obj instanceof List<?>})</li>
     *   <li>We <strong>cannot</strong> check if a list contains a specific element type 
     *       ({@code obj instanceof List<FlightRecommendation>})</li>
     *   <li>The workaround is to check individual elements if the list is non-empty</li>
     * </ul>
     * 
     * <h3>Type Erasure Explained</h3>
     * <p>During compilation, Java erases generic type information:</p>
     * <ul>
     *   <li>{@code List<String>} becomes just {@code List}</li>
     *   <li>{@code List<Integer>} becomes just {@code List}</li>
     *   <li>Type parameters are replaced with their bounds or Object</li>
     * </ul>
     * 
     * <p>This means at runtime, a {@code List<String>} and a {@code List<Integer>} 
     * are indistinguishable - they're both just {@code List} objects.</p>
     * 
     * <p>For the OCP exam, understand that:</p>
     * <pre>{@code
     * // This is allowed
     * if (obj instanceof List<?>) { ... }
     * 
     * // This is NOT allowed - won't compile
     * if (obj instanceof List<String>) { ... }
     * }</pre>
     * 
     * @param obj the object to check
     * @return true if the object is a non-empty list whose first element is a Recommendation
     */
    public static boolean isRecommendationList(Object obj) {
        // This is allowed: check if obj is a List
        if (obj instanceof List<?> list) {
            // But this is not possible due to type erasure:
            // if (obj instanceof List<FlightRecommendation>)
            
            // We can only check individual elements
            if (!list.isEmpty() && list.getFirst() instanceof Recommendation) {
                return true;
            }
        }
        return false;
    }
}