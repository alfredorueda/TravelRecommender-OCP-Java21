package com.edreams.travelrecommender.model;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A generic wrapper class for safely encapsulating any recommendation in a type-safe container.
 * 
 * <h2>Purpose in Architecture</h2>
 * {@code RecommendationBox<T>} serves as a monad-like container that provides:
 * <ul>
 *   <li>Type safety when working with different recommendation subtypes</li>
 *   <li>Safe transformation operations through {@code map()} and {@code filter()}</li>
 *   <li>Protection from null values and type casting errors</li>
 *   <li>A consistent API for manipulating recommendations regardless of their specific subtype</li>
 * </ul>
 * 
 * <h2>OCP Java 21 Learning Points</h2>
 * <ul>
 *   <li><strong>Bounded Type Parameters</strong>: The {@code <T extends Recommendation>} syntax 
 *       restricts the generic type to only permit subtypes of {@code Recommendation}.</li>
 *   <li><strong>Type Safety</strong>: Enforces that only valid recommendation types can be
 *       handled by this container, preventing ClassCastExceptions at runtime.</li>
 *   <li><strong>Immutability</strong>: The contained recommendation is final, promoting
 *       functional programming patterns and thread safety.</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * // Create a box containing a FlightRecommendation
 * FlightRecommendation flight = new FlightRecommendation(...);
 * RecommendationBox<FlightRecommendation> flightBox = new RecommendationBox<>(flight);
 * 
 * // Extract specific data using map() without breaking the container
 * String departureAirport = flightBox.map(f -> f.departureAirport());
 * 
 * // Create a box with a more general type parameter
 * RecommendationBox<Recommendation> genericBox = new RecommendationBox<>(flight);
 * 
 * // Safely attempt to cast it back to a specific type
 * Optional<RecommendationBox<FlightRecommendation>> optFlightBox = 
 *     genericBox.castTo(FlightRecommendation.class);
 * }</pre>
 * 
 * @param <T> the type of recommendation contained in this box, must be a subtype of {@code Recommendation}
 */
public class RecommendationBox<T extends Recommendation> {
    private final T recommendation;
    
    /**
     * Creates a new recommendation box with the given recommendation.
     * 
     * <p>This constructor enforces non-null values, which is a key aspect of container
     * types like this one. By rejecting null values at construction time, all methods
     * can safely operate on the contained recommendation without null checks.</p>
     * 
     * <p><strong>OCP Exam Note:</strong> This is a common pattern in Java APIs like
     * {@code Optional<T>} that aim to eliminate NullPointerExceptions.</p>
     * 
     * @param recommendation the recommendation to encapsulate
     * @throws IllegalArgumentException if the recommendation is null
     */
    public RecommendationBox(T recommendation) {
        if (recommendation == null) {
            throw new IllegalArgumentException("Recommendation cannot be null");
        }
        this.recommendation = recommendation;
    }
    
    /**
     * Returns the encapsulated recommendation.
     * 
     * <p>This accessor method provides direct access to the contained recommendation
     * with its specific type {@code T} preserved. This maintains type safety throughout
     * the application when working with specific recommendation types.</p>
     * 
     * <p><strong>Example:</strong></p>
     * <pre>{@code
     * RecommendationBox<FlightRecommendation> box = new RecommendationBox<>(flight);
     * FlightRecommendation flight = box.get();  // Type-safe, no casting needed
     * String airline = flight.airline();        // Access specific FlightRecommendation methods
     * }</pre>
     * 
     * @return the recommendation with its specific type preserved
     */
    public T get() {
        return recommendation;
    }
    
    /**
     * Returns the ID of the encapsulated recommendation.
     * 
     * <p>This is a convenience method that delegates to the contained recommendation.
     * It demonstrates how wrapper classes can selectively expose methods from their
     * wrapped objects, creating a more focused API.</p>
     * 
     * <p><strong>OCP Exam Note:</strong> This delegation pattern is common in wrapper
     * classes and the Decorator design pattern.</p>
     * 
     * @return the recommendation ID
     */
    public String getId() {
        return recommendation.getId();
    }
    
    /**
     * Returns true if the recommendation's confidence score exceeds the given threshold.
     * 
     * <p>This method demonstrates how the wrapper can provide domain-specific
     * functionality that adds value beyond just containing the recommendation.</p>
     * 
     * <p><strong>Example:</strong></p>
     * <pre>{@code
     * List<RecommendationBox<Recommendation>> recommendations = getRecommendations();
     * List<RecommendationBox<Recommendation>> highConfidence = 
     *     recommendations.stream()
     *                  .filter(box -> box.hasHighConfidence(0.8))
     *                  .collect(Collectors.toList());
     * }</pre>
     * 
     * @param threshold the confidence threshold (between 0.0 and 1.0)
     * @return true if confidence score exceeds threshold
     */
    public boolean hasHighConfidence(double threshold) {
        return recommendation.getConfidenceScore() >= threshold;
    }
    
    /**
     * Maps the encapsulated recommendation to a result of type R.
     * 
     * <p>This method is a core aspect of the container's functionality, allowing
     * transformations of the contained value without breaking the container's type
     * safety. It follows the Functor pattern from functional programming.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> This demonstrates several advanced concepts:</p>
     * <ul>
     *   <li><strong>Generic methods</strong> with a type parameter different from the class's type parameter</li>
     *   <li><strong>PECS principle</strong> (Producer Extends, Consumer Super) in action with {@code ? super T}</li>
     *   <li><strong>Function composition</strong> using Java's functional interfaces</li>
     * </ul>
     * 
     * <p><strong>Examples:</strong></p>
     * <pre>{@code
     * // Extract the airline from a FlightRecommendation
     * RecommendationBox<FlightRecommendation> flightBox = new RecommendationBox<>(flight);
     * String airline = flightBox.map(FlightRecommendation::airline);
     * 
     * // Extract price from any recommendation type through the interface method
     * RecommendationBox<Recommendation> box = new RecommendationBox<>(someRecommendation);
     * String title = box.map(Recommendation::getTitle);
     * }</pre>
     * 
     * <p><strong>Gotcha:</strong> The mapper function must handle the specific type T,
     * but due to the {@code ? super T} wildcard, you can pass a function that accepts
     * any supertype of T, including {@code Recommendation} itself.</p>
     * 
     * @param <R> the result type that the recommendation will be transformed to
     * @param mapper the mapping function that transforms the recommendation to type R
     * @return the result of applying the mapper to the recommendation
     */
    public <R> R map(Function<? super T, ? extends R> mapper) {
        return mapper.apply(recommendation);
    }
    
    /**
     * Executes the given consumer with the encapsulated recommendation.
     * 
     * <p>This method enables side-effect operations on the contained recommendation
     * without breaking the container pattern. It's useful for operations that don't
     * return a value, such as logging or updating UI components.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> This demonstrates the use of bounded wildcards
     * with {@code super}. The {@code ? super T} wildcard allows the consumer to accept
     * any supertype of T, following the PECS principle where Consumer is a "consumer" of T.</p>
     * 
     * <p><strong>Example:</strong></p>
     * <pre>{@code
     * // Print details for any recommendation type
     * Consumer<Recommendation> printer = rec -> System.out.println(rec.getTitle());
     * 
     * // Works with specific types due to the ? super T wildcard
     * RecommendationBox<FlightRecommendation> flightBox = new RecommendationBox<>(flight);
     * flightBox.ifPresent(printer);  // Printer accepts Recommendation, a supertype of FlightRecommendation
     * 
     * // Also works with specialized consumers
     * Consumer<FlightRecommendation> flightPrinter = flight -> 
     *     System.out.println(flight.airline() + " flight to " + flight.arrivalAirport());
     * flightBox.ifPresent(flightPrinter);
     * }</pre>
     * 
     * <p><strong>Gotcha:</strong> Because of the {@code ? super T} wildcard, the consumer
     * can't assume T is exactly the type it expects - it must be prepared to handle
     * any subtype of the expected type. This is usually not an issue when the consumer
     * is using methods from the Recommendation interface.</p>
     * 
     * @param consumer the consumer to execute with the contained recommendation
     */
    public void ifPresent(Consumer<? super T> consumer) {
        consumer.accept(recommendation);
    }
    
    /**
     * Returns an optional containing the encapsulated recommendation if its confidence
     * score exceeds the given threshold, otherwise returns an empty optional.
     * 
     * <p>This method combines filtering logic with the Optional container pattern,
     * allowing for fluent method chaining and avoiding null checks.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> This demonstrates integration with 
     * Java's built-in {@code Optional<T>} API, which is another container type
     * that serves a complementary purpose to RecommendationBox.</p>
     * 
     * <p><strong>Example:</strong></p>
     * <pre>{@code
     * RecommendationBox<HotelRecommendation> hotelBox = new RecommendationBox<>(hotel);
     * 
     * // Fluent chaining with Optional's methods
     * String hotelName = hotelBox.filter(0.8)
     *                          .map(hotel -> hotel.hotelName())
     *                          .orElse("No high confidence hotel found");
     * }</pre>
     * 
     * @param threshold the confidence threshold (between 0.0 and 1.0)
     * @return an optional containing the recommendation if confidence score exceeds threshold
     */
    public Optional<T> filter(double threshold) {
        return recommendation.getConfidenceScore() >= threshold
                ? Optional.of(recommendation)
                : Optional.empty();
    }
    
    /**
     * Creates a new RecommendationBox from the given recommendation.
     * 
     * <p>This static factory method provides an alternative, more concise way to 
     * create RecommendationBox instances, similar to Java's built-in factories like
     * {@code List.of()} or {@code Optional.of()}.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> This demonstrates a generic static factory method,
     * which requires its own type parameter declaration {@code <T extends Recommendation>}
     * because static methods cannot access the class's type parameter.</p>
     * 
     * <p><strong>Example:</strong></p>
     * <pre>{@code
     * // Instead of:
     * RecommendationBox<ActivityRecommendation> box1 = new RecommendationBox<>(activity);
     * 
     * // You can use:
     * RecommendationBox<ActivityRecommendation> box2 = RecommendationBox.of(activity);
     * 
     * // With type inference:
     * var box3 = RecommendationBox.of(activity);  // Type is inferred as RecommendationBox<ActivityRecommendation>
     * }</pre>
     * 
     * @param <T> the type of recommendation
     * @param recommendation the recommendation to encapsulate
     * @return a new RecommendationBox containing the given recommendation
     * @throws IllegalArgumentException if the recommendation is null
     */
    public static <T extends Recommendation> RecommendationBox<T> of(T recommendation) {
        return new RecommendationBox<>(recommendation);
    }
    
    /**
     * Safely casts this RecommendationBox to a box of the given type if the contained
     * recommendation is an instance of that type.
     * 
     * <p>This method solves a common problem in generic programming: safely downcasting
     * from a more general generic type to a more specific one. It returns an Optional
     * to avoid exceptions when the cast is not valid.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> This demonstrates several advanced features:</p>
     * <ul>
     *   <li><strong>Bounded type parameters</strong> with {@code <U extends Recommendation>}</li>
     *   <li><strong>Safe type casting</strong> using {@code Class.cast()} to avoid unchecked warnings</li>
     *   <li><strong>Optional API</strong> for representing potentially absent values</li>
     * </ul>
     * 
     * <p><strong>Example:</strong></p>
     * <pre>{@code
     * // Start with a box of the base type
     * RecommendationBox<Recommendation> genericBox = getRecommendation();
     * 
     * // Safely try to get a FlightRecommendation
     * Optional<RecommendationBox<FlightRecommendation>> flightBoxOpt = 
     *     genericBox.castTo(FlightRecommendation.class);
     *     
     * // Use the Optional API to handle the result
     * flightBoxOpt.ifPresent(flightBox -> {
     *     FlightRecommendation flight = flightBox.get();
     *     System.out.println("Found flight to " + flight.arrivalAirport());
     * });
     * }</pre>
     * 
     * <p><strong>Gotcha:</strong> Due to type erasure, this method cannot work with
     * parameterized types like {@code List<String>}. It only works with reifiable types
     * that maintain their type information at runtime.</p>
     * 
     * @param <U> the target type, must be a subtype of Recommendation
     * @param clazz the Class object representing the target type
     * @return an optional containing the cast box if the cast is valid, or empty if invalid
     */
    public <U extends Recommendation> Optional<RecommendationBox<U>> castTo(Class<U> clazz) {
        if (clazz.isInstance(recommendation)) {
            // Use the proper Java way to cast using Class.cast() method to avoid unchecked casts
            U castedRecommendation = clazz.cast(recommendation);
            return Optional.of(new RecommendationBox<>(castedRecommendation));
        }
        return Optional.empty();
    }
}