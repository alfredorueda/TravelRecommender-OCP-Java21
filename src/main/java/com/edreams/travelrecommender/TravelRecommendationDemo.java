package com.edreams.travelrecommender;

import com.edreams.travelrecommender.model.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Main demonstration class showcasing the Travel Recommendation System's capabilities.
 * 
 * <h2>Purpose in Architecture</h2>
 * <p>This class serves as a practical demonstration of how all components in the
 * travel recommendation system work together. It creates sample data, exercises
 * the core functionality, and illustrates advanced Java 21 features through concrete
 * examples that instructors can use to explain concepts to students preparing for
 * the OCP Java 21 certification.</p>
 * 
 * <h2>OCP Java 21 Features Demonstrated</h2>
 * <p>This class provides working examples of:</p>
 * <ul>
 *   <li><strong>Records</strong>: Creating and working with immutable data carriers</li>
 *   <li><strong>Sealed Types</strong>: Working with a closed type hierarchy</li>
 *   <li><strong>Pattern Matching</strong>: Both for instanceof and switch expressions</li>
 *   <li><strong>Generics with Wildcards</strong>: Illustrating the PECS principle</li>
 *   <li><strong>Type Erasure</strong>: Showing runtime limitations of Java's generic system</li>
 *   <li><strong>Functional Programming</strong>: Using lambda expressions and method references</li>
 *   <li><strong>List.getFirst()</strong>: Using the new Java 21 collection API method</li>
 * </ul>
 * 
 * <h2>Teaching Structure</h2>
 * <p>The demo is organized in sections that instructors can use to demonstrate specific concepts:</p>
 * <ol>
 *   <li>Creating sample data with records</li>
 *   <li>Working with generic collections of sealed types</li>
 *   <li>Using RecommendationBox to encapsulate recommendations</li>
 *   <li>Demonstrating the PECS principle with bounded wildcards</li>
 *   <li>Exploring pattern matching for type checking and data extraction</li>
 *   <li>Showing type erasure limitations in Java's generic system</li>
 * </ol>
 * 
 * <h2>Instructor Notes</h2>
 * <p>When teaching with this code:</p>
 * <ul>
 *   <li>Start by explaining the domain model with sealed hierarchy</li>
 *   <li>Show how records simplify data modeling for recommendations</li>
 *   <li>Highlight how pattern matching makes polymorphic code cleaner</li>
 *   <li>Explain wildcard usage with the "Producer Extends, Consumer Super" principle</li>
 *   <li>Demonstrate type erasure to help students understand generic type limitations</li>
 * </ul>
 */
public class TravelRecommendationDemo {
    /**
     * Main entry point for the demonstration.
     * 
     * <p>This method orchestrates the entire demonstration, creating sample data
     * and exercising the key features of the system. Each section is clearly
     * delineated with console output for educational clarity.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> Pay special attention to:</p>
     * <ul>
     *   <li>Type inference with {@code var} for local variables</li>
     *   <li>Explicit type parameter specification in {@code RecommendationService.<Recommendation>combineRecommendations()}</li>
     *   <li>The use of lambda expressions with forEach</li>
     *   <li>The new List.getFirst() method instead of get(0)</li>
     * </ul>
     * 
     * @param args command line arguments (not used)
     */
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
        demonstrateRecommendationBox(flights.getFirst(), hotels.getFirst(), activities.getFirst(), packages.getFirst());
        
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
     * Creates a collection of sample flight recommendations for demonstration purposes.
     * 
     * <p>This method demonstrates how to create multiple instances of the {@link FlightRecommendation}
     * record with various attributes representing different types of flights.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> Note the use of:</p>
     * <ul>
     *   <li>Record instantiation with the canonical constructor</li>
     *   <li>The immutability of records (all fields are final)</li>
     *   <li>The {@code List.of()} factory method for creating immutable lists</li>
     *   <li>Method chaining with LocalDateTime for date manipulation</li>
     * </ul>
     * 
     * @return an immutable list of sample flight recommendations
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
     * Creates a collection of sample hotel recommendations for demonstration purposes.
     * 
     * <p>This method demonstrates how to create multiple instances of the {@link HotelRecommendation}
     * record with different star ratings, amenities, and price points.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> Pay attention to how the compact constructor in 
     * {@link HotelRecommendation} validates that star ratings are between 1 and 5.</p>
     * 
     * @return an immutable list of sample hotel recommendations
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
     * Creates a collection of sample activity recommendations for demonstration purposes.
     * 
     * <p>This method demonstrates how to create multiple instances of the {@link ActivityRecommendation}
     * record representing various tourist activities with different durations, categories, and
     * characteristics.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> Note the use of:</p>
     * <ul>
     *   <li>The Duration class from the java.time package for representing time spans</li>
     *   <li>Different combinations of categories to demonstrate the getActivityType() method</li>
     *   <li>Boolean flags (isIndoor) that influence classification</li>
     * </ul>
     * 
     * @return an immutable list of sample activity recommendations
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
     * Creates a collection of sample package recommendations by combining flights, hotels, and activities.
     * 
     * <p>This method demonstrates the composite nature of the {@link PackageRecommendation} record,
     * which references other recommendation types to create bundled travel packages.</p>
     * 
     * <p><strong>OCP Java 21 Note:</strong> Note how PackageRecommendation implements the Composite
     * design pattern with records, creating a tree-like structure of recommendation objects.
     * This demonstrates advanced object composition techniques.</p>
     * 
     * @param flights list of flight recommendations to include in packages
     * @param hotels list of hotel recommendations to include in packages
     * @param activities list of activity recommendations to include in packages
     * @return an immutable list of sample package recommendations
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
            flights.getFirst(),
            hotels.getFirst(),
            List.of(activities.getFirst(), activities.get(3)),
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
     * Demonstrates the use of {@link RecommendationBox} class with different recommendation types
     * and shows pattern matching for type detection and data extraction.
     * 
     * <p>This method showcases how a generic container like RecommendationBox can
     * provide type-safe operations on different recommendation subtypes while
     * maintaining a consistent API.</p>
     * 
     * <p><strong>OCP Java 21 Features:</strong></p>
     * <ul>
     *   <li><strong>Pattern Matching with instanceof</strong>: Using the pattern variable binding
     *       syntax to simultaneously check types and extract values</li>
     *   <li><strong>Generic Methods</strong>: Using the map() method to transform recommendations</li>
     *   <li><strong>Method References</strong>: Using Recommendation::getTitle syntax</li>
     *   <li><strong>Static Factory Methods</strong>: Using RecommendationBox.of() as an alternative
     *       constructor</li>
     * </ul>
     * 
     * <p>Compare the pattern matching approach:</p>
     * <pre>{@code
     * if (box.get() instanceof FlightRecommendation f) {
     *     // Use f directly
     * }
     * }</pre>
     * 
     * <p>With the pre-Java 16 approach:</p>
     * <pre>{@code
     * if (box.get() instanceof FlightRecommendation) {
     *     FlightRecommendation f = (FlightRecommendation) box.get();
     *     // Use f after casting
     * }
     * }</pre>
     * 
     * @param flight a sample flight recommendation
     * @param hotel a sample hotel recommendation
     * @param activity a sample activity recommendation
     * @param packageRec a sample package recommendation
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
     * Demonstrates the PECS principle (Producer Extends, Consumer Super) with bounded wildcards.
     * 
     * <p>This method provides concrete examples of how to use bounded wildcards
     * properly based on whether a collection is used as a producer of values or
     * a consumer of values.</p>
     * 
     * <p><strong>OCP Java 21 Feature: Bounded Wildcards</strong></p>
     * <p>This method demonstrates two core concepts:</p>
     * <ul>
     *   <li><strong>Producer Extends</strong>: When you only read from a collection,
     *       use {@code ? extends T} to allow reading from any subtype of T</li>
     *   <li><strong>Consumer Super</strong>: When you only write to a collection,
     *       use {@code ? super T} to allow writing to any supertype of T</li>
     * </ul>
     * 
     * <p>The method also shows which operations are allowed and which are restricted
     * when using each type of bounded wildcard. This is a crucial concept for the OCP
     * Java 21 exam.</p>
     * 
     * <p><strong>OCP Exam Note:</strong> Remember these restrictions:</p>
     * <ul>
     *   <li>With {@code List<? extends T>}, you can read T objects but cannot add any objects</li>
     *   <li>With {@code List<? super T>}, you can add T objects but can only read Object instances</li>
     * </ul>
     * 
     * @param flights a list of flight recommendations (specific subtype)
     * @param hotels a list of hotel recommendations (specific subtype)
     * @param activities a list of activity recommendations (specific subtype)
     */
    private static void demonstratePecs(
            List<FlightRecommendation> flights,
            List<HotelRecommendation> hotels,
            List<ActivityRecommendation> activities) {
        
        // Producer Extends: Using "? extends" when reading from collections
        List<? extends Recommendation> recommendations = flights; // We can assign flights to this list
        Recommendation rec = recommendations.getFirst(); // We can read Recommendations from it
        System.out.println(" - Producer (extends): Reading " + rec.getTitle());
        
        // But we can't add to this list because we don't know the exact type
        // recommendations.add(hotels.getFirst()); // This would not compile
        
        // Consumer Super: Using "? super" when adding to collections
        List<? super FlightRecommendation> flightList = new ArrayList<Recommendation>();
        flightList.add(flights.getFirst()); // We can add FlightRecommendations to this list
        System.out.println(" - Consumer (super): Added flight to a list of super type");
        
        // But we can't read specific types from it
        // FlightRecommendation flight = flightList.getFirst(); // This would not compile
        
        // Demonstrate addRecommendation method with PECS
        List<Recommendation> allRecs = new ArrayList<>();
        RecommendationService.addRecommendation(allRecs, flights.getFirst());
        RecommendationService.addRecommendation(allRecs, hotels.getFirst());
        RecommendationService.addRecommendation(allRecs, activities.getFirst());
        
        System.out.println(" - Added different recommendation types using PECS: " + allRecs.size() + " items");
    }
    
    /**
     * Demonstrates the limitations of Java's generic type system due to type erasure.
     * 
     * <p>This method illustrates how Java's type erasure affects runtime type checking
     * with generics, showing what kind of type information is preserved and what
     * is lost at runtime.</p>
     * 
     * <p><strong>OCP Java 21 Feature: Type Erasure</strong></p>
     * <p>This demonstrates one of the most important limitations to understand for the OCP exam:</p>
     * <ul>
     *   <li>At compile time, generic type parameters ensure type safety</li>
     *   <li>At runtime, all generic type information is erased</li>
     *   <li>A {@code List<String>} and {@code List<Integer>} are indistinguishable at runtime</li>
     *   <li>You can check if an object is a List, but not what type of elements it contains</li>
     * </ul>
     * 
     * <p><strong>OCP Exam Notes:</strong></p>
     * <ul>
     *   <li>Valid: {@code obj instanceof List<?>}</li>
     *   <li>Invalid: {@code obj instanceof List<String>} (won't compile)</li>
     *   <li>Workaround: Check the list elements individually</li>
     * </ul>
     * 
     * <p>This limitation of Java's generic type system is a direct consequence of
     * how generics were implemented to maintain backward compatibility.</p>
     * 
     * @param flights a list of flight recommendations
     * @param hotels a list of hotel recommendations
     * @param recommendations a mixed list of various recommendation types
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