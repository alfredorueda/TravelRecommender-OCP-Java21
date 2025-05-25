package com.edreams.travelrecommender.model;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A generic wrapper class for safely encapsulating any recommendation.
 * 
 * OCP Java 21 Note: This class demonstrates the use of bounded type parameters
 * with extends to restrict the generic type to subtypes of Recommendation.
 * 
 * @param <T> the type of recommendation contained in this box, must be a subtype of Recommendation
 */
public class RecommendationBox<T extends Recommendation> {
    private final T recommendation;
    
    /**
     * Creates a new recommendation box with the given recommendation.
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
     * @return the recommendation
     */
    public T get() {
        return recommendation;
    }
    
    /**
     * Returns the ID of the encapsulated recommendation.
     * 
     * @return the recommendation ID
     */
    public String getId() {
        return recommendation.getId();
    }
    
    /**
     * Returns true if the recommendation's confidence score exceeds the given threshold.
     * 
     * @param threshold the confidence threshold
     * @return true if confidence score exceeds threshold
     */
    public boolean hasHighConfidence(double threshold) {
        return recommendation.getConfidenceScore() >= threshold;
    }
    
    /**
     * Maps the encapsulated recommendation to a result of type R.
     * 
     * OCP Java 21 Note: This demonstrates generic methods with a type parameter
     * different from the class's type parameter.
     * 
     * @param <R> the result type
     * @param mapper the mapping function
     * @return the result of applying the mapper to the recommendation
     */
    public <R> R map(Function<? super T, ? extends R> mapper) {
        return mapper.apply(recommendation);
    }
    
    /**
     * Executes the given consumer with the encapsulated recommendation.
     * 
     * OCP Java 21 Note: This demonstrates the use of bounded wildcards with super.
     * 
     * @param consumer the consumer to execute
     */
    public void ifPresent(Consumer<? super T> consumer) {
        consumer.accept(recommendation);
    }
    
    /**
     * Returns an optional containing the encapsulated recommendation if its confidence
     * score exceeds the given threshold, otherwise returns an empty optional.
     * 
     * @param threshold the confidence threshold
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
     * OCP Java 21 Note: This demonstrates a generic static factory method.
     * 
     * @param <T> the type of recommendation
     * @param recommendation the recommendation to encapsulate
     * @return a new RecommendationBox
     */
    public static <T extends Recommendation> RecommendationBox<T> of(T recommendation) {
        return new RecommendationBox<>(recommendation);
    }
    
    /**
     * Safely casts this RecommendationBox to a box of the given type if the contained
     * recommendation is an instance of that type.
     * 
     * OCP Java 21 Note: This demonstrates pattern matching with instanceof
     * and safe casting with generics.
     * 
     * @param <U> the target type
     * @param clazz the class object of the target type
     * @return an optional containing the cast box if the cast is valid
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