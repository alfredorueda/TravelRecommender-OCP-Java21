package com.edreams.travelrecommender;

import com.edreams.travelrecommender.model.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Service class for handling travel recommendations.
 * 
 * OCP Java 21 Note: This class demonstrates various advanced Java 21 features including:
 * - Generic methods with bounded wildcards
 * - PECS principle (Producer Extends, Consumer Super)
 * - Pattern matching for switch
 * - Type erasure limitations
 */
public class RecommendationService {

    /**
     * Filters recommendations by a given predicate.
     * 
     * OCP Java 21 Note: This demonstrates the use of bounded wildcards with extends
     * for a producer method (PECS principle).
     * 
     * @param <T> the type of recommendation
     * @param recommendations the list of recommendations to filter
     * @param predicate the predicate to apply
     * @return a filtered list of recommendations
     */
    public static <T extends Recommendation> List<T> filterByPredicate(
            List<? extends T> recommendations, 
            Predicate<? super T> predicate) {
        return recommendations.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    /**
     * Filters recommendations by confidence score.
     * 
     * OCP Java 21 Note: This demonstrates a more specific application of the generic method.
     * 
     * @param <T> the type of recommendation
     * @param recommendations the list of recommendations to filter
     * @param minimumConfidence the minimum confidence score
     * @return a filtered list of recommendations
     */
    public static <T extends Recommendation> List<T> filterByConfidence(
            List<? extends T> recommendations, 
            double minimumConfidence) {
        return filterByPredicate(recommendations, 
                rec -> rec.getConfidenceScore() >= minimumConfidence);
    }
    
    /**
     * Combines multiple lists of recommendations into a single list.
     * 
     * OCP Java 21 Note: This demonstrates type erasure in generics.
     * At runtime, all the generic type information is erased, and this method
     * actually processes a List of Recommendations.
     * 
     * @param <T> the type of recommendation
     * @param lists the lists to combine
     * @return a combined list of recommendations
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
     * Safely adds a recommendation to a list.
     * 
     * OCP Java 21 Note: This demonstrates the use of bounded wildcards with super
     * for a consumer method (PECS principle).
     * 
     * @param <T> the type of recommendation
     * @param recommendations the list to add to
     * @param recommendation the recommendation to add
     */
    public static <T extends Recommendation> void addRecommendation(
            List<? super T> recommendations, 
            T recommendation) {
        recommendations.add(recommendation);
    }
    
    /**
     * Returns a description of the recommendation based on its type.
     * 
     * OCP Java 21 Note: This demonstrates pattern matching for switch statements,
     * which is a powerful feature of Java 21 for handling different record types.
     * 
     * @param recommendation the recommendation to describe
     * @return a description of the recommendation
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
     * Categorizes recommendations into separate collections by type.
     * 
     * OCP Java 21 Note: This demonstrates the use of instanceof pattern matching,
     * which eliminates the need for explicit casting.
     * 
     * @param recommendations the recommendations to categorize
     * @return a map of recommendation types to lists of recommendations
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
     * Demonstrates a key limitation of type erasure in Java generics.
     * 
     * OCP Java 21 Note: Due to type erasure, we cannot check if a list is
     * a List<FlightRecommendation> or a List<HotelRecommendation> at runtime.
     * All we can check is if it's a List of some kind.
     * 
     * @param obj the object to check
     * @return true if the object is a list of recommendations
     */
    public static boolean isRecommendationList(Object obj) {
        // This is allowed: check if obj is a List
        if (obj instanceof List<?> list) {
            // But this is not possible due to type erasure:
            // if (obj instanceof List<FlightRecommendation>)
            
            // We can only check individual elements
            if (!list.isEmpty() && list.get(0) instanceof Recommendation) {
                return true;
            }
        }
        return false;
    }
}