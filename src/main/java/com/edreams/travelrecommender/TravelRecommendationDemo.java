package com.edreams.travelrecommender;

import com.edreams.travelrecommender.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Main demonstration class for the Travel Recommendation System.
 * 
 * This class creates sample recommendations and demonstrates the use of:
 * - Generic records
 * - Generic methods with bounded wildcards
 * - Pattern matching for switch and instanceof
 * - PECS principle
 * - Type erasure considerations
 */
public class TravelRecommendationDemo {

    public static void main(String[] args) {
        System.out.println("Travel Recommendation System - OCP Java 21 Demo");
        System.out.println("=================================================");
        
        // Create sample flight recommendations
        var flights = createSampleFlights();
        System.out.println("\n🛫 Flight Recommendations: " + flights.size());
        
        // Create sample hotel recommendations
        var hotels = createSampleHotels();
        System.out.println("\n🏨 Hotel Recommendations: " + hotels.size());
        
        // Create sample activity recommendations
        var activities = createSampleActivities();
        System.out.println("\n🏄 Activity Recommendations: " + activities.size());
        
        // Create a package recommendation
        var packages = createSamplePackages(flights, hotels, activities);
        System.out.println("\n📦 Package Recommendations: " + packages.size());
        
        // Fix the type parameter to explicitly use Recommendation
        // This avoids the compiler inferring a more specific common type
        List<Recommendation> allRecommendations = RecommendationService.<Recommendation>combineRecommendations(
                flights, hotels, activities, packages);
        System.out.println("\n📋 Total Recommendations: " + allRecommendations.size());
        
        // Filter recommendations by confidence score
        double minimumConfidence = 0.7;
        var highConfidenceRecommendations = RecommendationService
                .filterByConfidence(allRecommendations, minimumConfidence);
        
        System.out.println("\n⭐ High Confidence Recommendations (>= " + minimumConfidence + "): " 
                + highConfidenceRecommendations.size());
        
        // Demonstrate RecommendationBox with pattern matching
        System.out.println("\n📦 Recommendation Box Demo:");
        demonstrateRecommendationBox(flights.get(0), hotels.get(0), activities.get(0), packages.get(0));
        
        // Demonstrate pattern matching with instanceof and switch
        System.out.println("\n🔍 Pattern Matching Demo:");
        for (Recommendation rec : highConfidenceRecommendations.subList(0, 
                Math.min(4, highConfidenceRecommendations.size()))) {
            System.out.println(" - " + RecommendationService.describeRecommendation(rec));
        }
        
        // Demonstrate PECS (Producer Extends, Consumer Super)
        System.out.println("\n🧩 PECS Principle Demo:");
        demonstratePecs(flights, hotels, activities);
        
        // Demonstrate categorizing recommendations - shows instanceof pattern matching
        System.out.println("\n📊 Categorized Recommendations:");
        Map<String, List<Recommendation>> categorized = 
                RecommendationService.categorizeRecommendations(allRecommendations);
        
        categorized.forEach((category, recs) -> 
            System.out.println(" - " + category + ": " + recs.size()));
            
        // Demonstrate type erasure limitations
        System.out.println("\n⚠️ Type Erasure Limitations:");
        demonstrateTypeErasure(flights, hotels, allRecommendations);
    }
    
    /**
     * Creates sample flight recommendations.
     */
    private static List<FlightRecommendation> createSampleFlights() {
        var flight1 = new FlightRecommendation(
            "FL001", 
            "Budget flight to Paris",
            "Low-cost direct flight to Paris with basic amenities",
            0.85,
            "JFK", 
            "CDG",
            LocalDateTime.now().plusDays(10).withHour(9).withMinute(0),
            LocalDateTime.now().plusDays(10).withHour(22).withMinute(30),
            "Air France", 
            true,
            List.of("Wi-Fi", "Meal included"),
            450.99
        );
        
        var flight2 = new FlightRecommendation(
            "FL002",
            "Business class to London",
            "Premium business class experience with dedicated service",
            0.92,
            "LAX",
            "LHR",
            LocalDateTime.now().plusDays(15).withHour(18).withMinute(30),
            LocalDateTime.now().plusDays(16).withHour(13).withMinute(15),
            "British Airways",
            true,
            List.of("Lie-flat seats", "Lounge access", "Premium meals", "Priority boarding"),
            1250.50
        );
        
        var flight3 = new FlightRecommendation(
            "FL003",
            "Economy flight to Tokyo",
            "Connecting economy flight to Tokyo with one stop in Seoul",
            0.68,
            "SFO",
            "NRT",
            LocalDateTime.now().plusDays(20).withHour(11).withMinute(15),
            LocalDateTime.now().plusDays(21).withHour(8).withMinute(45),
            "Korean Air",
            false,
            List.of("In-flight entertainment", "USB charging"),
            875.25
        );
        
        return List.of(flight1, flight2, flight3);
    }
    
    /**
     * Creates sample hotel recommendations.
     */
    private static List<HotelRecommendation> createSampleHotels() {
        var hotel1 = new HotelRecommendation(
            "HT001",
            "Luxury stay in central Paris",
            "5-star hotel experience in the heart of Paris with Eiffel Tower views",
            0.78,
            "Parisian Luxury Hotel",
            5,
            "Paris, France",
            List.of("Spa", "Pool", "Fine dining", "Concierge", "Room service"),
            320.00,
            0.5,
            true
        );
        
        var hotel2 = new HotelRecommendation(
            "HT002",
            "Family-friendly resort in London",
            "Kid-friendly 4-star hotel with family amenities and spacious rooms",
            0.82,
            "London Family Resort",
            4,
            "London, UK",
            List.of("Kids club", "Pool", "Game room", "Family dining", "Babysitting"),
            240.50,
            2.0,
            true
        );
        
        var hotel3 = new HotelRecommendation(
            "HT003",
            "Budget stay in Tokyo",
            "Clean, comfortable 3-star hotel in Tokyo with great public transport links",
            0.65,
            "Tokyo Budget Inn",
            3,
            "Tokyo, Japan",
            List.of("Free Wi-Fi", "Breakfast included", "Laundry service"),
            150.75,
            3.5,
            false
        );
        
        return List.of(hotel1, hotel2, hotel3);
    }
    
    /**
     * Creates sample activity recommendations.
     */
    private static List<ActivityRecommendation> createSampleActivities() {
        var activity1 = new ActivityRecommendation(
            "AC001",
            "Skip-the-line Louvre Museum tour",
            "Guided tour of the world-famous Louvre Museum with priority access",
            0.91,
            "Louvre Museum Guided Tour",
            "Paris, France",
            Duration.ofHours(3),
            List.of("Cultural", "Museum", "Art"),
            true,
            65.50,
            8
        );
        
        var activity2 = new ActivityRecommendation(
            "AC002",
            "London Eye experience",
            "Spectacular views of London from the iconic London Eye",
            0.85,
            "London Eye Ticket",
            "London, UK",
            Duration.ofMinutes(30),
            List.of("Sightseeing", "Family-friendly"),
            false,
            32.00,
            5
        );
        
        var activity3 = new ActivityRecommendation(
            "AC003",
            "Tokyo street food tour",
            "Culinary adventure through Tokyo's vibrant street food scene",
            0.79,
            "Tokyo Street Food Experience",
            "Tokyo, Japan",
            Duration.ofHours(4),
            List.of("Food", "Cultural", "Walking tour"),
            false,
            95.00,
            12
        );
        
        var activity4 = new ActivityRecommendation(
            "AC004",
            "Seine River dinner cruise",
            "Romantic dinner cruise along the Seine with views of illuminated Paris landmarks",
            0.72,
            "Seine Dinner Cruise",
            "Paris, France",
            Duration.ofHours(2).plusMinutes(30),
            List.of("Romantic", "Dining", "Sightseeing"),
            true,
            120.00,
            18
        );
        
        return List.of(activity1, activity2, activity3, activity4);
    }
    
    /**
     * Creates sample package recommendations.
     */
    private static List<PackageRecommendation> createSamplePackages(
            List<FlightRecommendation> flights,
            List<HotelRecommendation> hotels,
            List<ActivityRecommendation> activities) {
        
        var package1 = new PackageRecommendation(
            "PK001",
            "Romantic Paris Getaway",
            "All-inclusive romantic weekend in Paris with luxury accommodations",
            0.88,
            flights.get(0),
            hotels.get(0),
            List.of(activities.get(0), activities.get(3)),
            0.15,
            950.00
        );
        
        var package2 = new PackageRecommendation(
            "PK002",
            "Family London Adventure",
            "Fun-filled family trip to London with kid-friendly activities",
            0.75,
            flights.get(1),
            hotels.get(1),
            List.of(activities.get(1)),
            0.10,
            1450.00
        );
        
        return List.of(package1, package2);
    }
    
    /**
     * Demonstrates the use of RecommendationBox with pattern matching.
     */
    private static void demonstrateRecommendationBox(
            FlightRecommendation flight,
            HotelRecommendation hotel,
            ActivityRecommendation activity,
            PackageRecommendation packageRec) {
        
        // Create a generic box with different recommendation types
        var flightBox = new RecommendationBox<>(flight);
        var hotelBox = RecommendationBox.of(hotel); // Using the static factory method
        var activityBox = new RecommendationBox<>(activity);
        var packageBox = new RecommendationBox<>(packageRec);
        
        // Create a list of generic recommendation boxes
        var boxes = List.of(flightBox, hotelBox, activityBox, packageBox);
        
        // Demonstrate pattern matching with instanceof
        for (var box : boxes) {
            if (box.get() instanceof FlightRecommendation f) {
                System.out.println(" - Flight box: " + f.airline() + " from " + f.departureAirport());
            } else if (box.get() instanceof HotelRecommendation h) {
                System.out.println(" - Hotel box: " + h.hotelName() + " (" + h.starRating() + "-star)");
            } else if (box.get() instanceof ActivityRecommendation a) {
                System.out.println(" - Activity box: " + a.activityName() + " (" + a.getFormattedDuration() + ")");
            } else if (box.get() instanceof PackageRecommendation p) {
                System.out.println(" - Package box: " + p.title() + " with " + p.activities().size() + " activities");
            }
        }
        
        // Demonstrate the generic map method
        String flightTitle = flightBox.map(Recommendation::getTitle);
        double hotelPrice = hotelBox.map(HotelRecommendation::pricePerNight);
        
        System.out.println(" - Mapped flight title: " + flightTitle);
        System.out.println(" - Mapped hotel price: $" + hotelPrice);
    }
    
    /**
     * Demonstrates the PECS principle (Producer Extends, Consumer Super).
     */
    private static void demonstratePecs(
            List<FlightRecommendation> flights,
            List<HotelRecommendation> hotels,
            List<ActivityRecommendation> activities) {
        
        // Producer Extends: Using "? extends" when reading from collections
        List<? extends Recommendation> recommendations = flights; // We can assign flights to this list
        Recommendation rec = recommendations.get(0); // We can read Recommendations from it
        System.out.println(" - Producer (extends): Reading " + rec.getTitle());
        
        // But we can't add to this list because we don't know the exact type
        // recommendations.add(hotels.get(0)); // This would not compile
        
        // Consumer Super: Using "? super" when adding to collections
        List<? super FlightRecommendation> flightList = new ArrayList<Recommendation>();
        flightList.add(flights.get(0)); // We can add FlightRecommendations to this list
        System.out.println(" - Consumer (super): Added flight to a list of super type");
        
        // But we can't read specific types from it
        // FlightRecommendation flight = flightList.get(0); // This would not compile
        
        // Demonstrate addRecommendation method with PECS
        List<Recommendation> allRecs = new ArrayList<>();
        RecommendationService.addRecommendation(allRecs, flights.get(0));
        RecommendationService.addRecommendation(allRecs, hotels.get(0));
        RecommendationService.addRecommendation(allRecs, activities.get(0));
        
        System.out.println(" - Added different recommendation types using PECS: " + allRecs.size() + " items");
    }
    
    /**
     * Demonstrates type erasure limitations in Java generics.
     */
    private static void demonstrateTypeErasure(
            List<FlightRecommendation> flights,
            List<HotelRecommendation> hotels,
            List<Recommendation> recommendations) {
        
        // Type erasure means generic type info is lost at runtime
        System.out.println(" - Type erasure demonstration:");
        
        System.out.println("   flights instanceof List<?>: " + (flights instanceof List<?>));
        System.out.println("   flights instanceof List: " + (flights instanceof List));
        
        // Cannot check specific generic types due to type erasure
        // System.out.println(flights instanceof List<FlightRecommendation>); // Won't compile
        
        // But we can check elements individually
        boolean allFlights = true;
        for (Object obj : recommendations) {
            if (!(obj instanceof FlightRecommendation)) {
                allFlights = false;
                break;
            }
        }
        System.out.println("   All recommendations are flights: " + allFlights);
        
        // Demonstrate the method that shows type erasure limitations
        System.out.println("   isRecommendationList(flights): " + 
                RecommendationService.isRecommendationList(flights));
        System.out.println("   isRecommendationList(hotels): " + 
                RecommendationService.isRecommendationList(hotels));
        System.out.println("   isRecommendationList(\"not a list\"): " + 
                RecommendationService.isRecommendationList("not a list"));
    }
}