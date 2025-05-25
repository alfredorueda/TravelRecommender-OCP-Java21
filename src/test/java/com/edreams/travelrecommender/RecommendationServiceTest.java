package com.edreams.travelrecommender;

import com.edreams.travelrecommender.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Travel Recommendation System.
 * 
 * OCP Java 21 Note: This test class demonstrates how to test various aspects
 * of a system using generics, sealed types, and pattern matching.
 */
@DisplayName("Travel Recommendation System Tests")
class RecommendationServiceTest {

    // Test data
    private FlightRecommendation flight;
    private HotelRecommendation hotel;
    private ActivityRecommendation activity;
    private PackageRecommendation packageRec;
    private List<Recommendation> allRecommendations;

    @BeforeEach
    void setUp() {
        // Create test flight
        flight = new FlightRecommendation(
            "F1", "Test Flight", "A test flight", 0.8,
            "SRC", "DST", LocalDateTime.now(), LocalDateTime.now().plusHours(5),
            "Test Airline", true, List.of("Wi-Fi"), 200.0
        );
        
        // Create test hotel
        hotel = new HotelRecommendation(
            "H1", "Test Hotel", "A test hotel", 0.7,
            "Test Hotel", 4, "Test Location", List.of("Pool"), 150.0, 1.0, true
        );
        
        // Create test activity
        activity = new ActivityRecommendation(
            "A1", "Test Activity", "A test activity", 0.6,
            "Test Activity", "Test Location", Duration.ofHours(2),
            List.of("Test"), false, 50.0, 10
        );
        
        // Create test package
        packageRec = new PackageRecommendation(
            "P1", "Test Package", "A test package", 0.9,
            flight, hotel, List.of(activity), 0.1, 380.0
        );
        
        // Combine all recommendations
        allRecommendations = List.of(flight, hotel, activity, packageRec);
    }

    @Nested
    @DisplayName("Generic Methods Tests")
    class GenericMethodsTests {
        
        @Test
        @DisplayName("Filter recommendations by confidence score")
        void testFilterByConfidence() {
            double threshold = 0.7;
            var highConfidenceRecs = RecommendationService.filterByConfidence(allRecommendations, threshold);
            
            assertEquals(3, highConfidenceRecs.size());
            assertTrue(highConfidenceRecs.contains(flight));
            assertTrue(highConfidenceRecs.contains(hotel));
            assertTrue(highConfidenceRecs.contains(packageRec));
            assertFalse(highConfidenceRecs.contains(activity));
            
            // Test with a list of a specific type
            List<FlightRecommendation> flights = List.of(flight);
            var highConfidenceFlights = RecommendationService.filterByConfidence(flights, threshold);
            assertEquals(1, highConfidenceFlights.size());
            assertEquals(flight, highConfidenceFlights.getFirst());
        }
        
        @Test
        @DisplayName("Combine recommendations from multiple lists")
        void testCombineRecommendations() {
            List<FlightRecommendation> flights = List.of(flight);
            List<HotelRecommendation> hotels = List.of(hotel);
            List<ActivityRecommendation> activities = List.of(activity);
            
            var combined = RecommendationService.combineRecommendations(flights, hotels, activities);
            
            assertEquals(3, combined.size());
            assertTrue(combined.contains(flight));
            assertTrue(combined.contains(hotel));
            assertTrue(combined.contains(activity));
        }
        
        @Test
        @DisplayName("Add recommendations with bounded wildcard super")
        void testAddRecommendation() {
            List<Recommendation> recommendations = new ArrayList<>();
            
            RecommendationService.addRecommendation(recommendations, flight);
            RecommendationService.addRecommendation(recommendations, hotel);
            RecommendationService.addRecommendation(recommendations, activity);
            
            assertEquals(3, recommendations.size());
            assertTrue(recommendations.contains(flight));
            assertTrue(recommendations.contains(hotel));
            assertTrue(recommendations.contains(activity));
            
            // Test with a more specific list
            List<FlightRecommendation> flights = new ArrayList<>();
            RecommendationService.addRecommendation(flights, flight);
            assertEquals(1, flights.size());
            assertEquals(flight, flights.getFirst());
        }
    }
    
    @Nested
    @DisplayName("Pattern Matching Tests")
    class PatternMatchingTests {
        
        @Test
        @DisplayName("Describe recommendations using pattern matching for switch")
        void testDescribeRecommendation() {
            String flightDesc = RecommendationService.describeRecommendation(flight);
            String hotelDesc = RecommendationService.describeRecommendation(hotel);
            String activityDesc = RecommendationService.describeRecommendation(activity);
            String packageDesc = RecommendationService.describeRecommendation(packageRec);
            
            assertTrue(flightDesc.contains("Flight from SRC to DST"));
            assertTrue(flightDesc.contains("Test Airline"));
            assertTrue(flightDesc.contains("Direct"));
            
            assertTrue(hotelDesc.contains("4-star hotel"));
            assertTrue(hotelDesc.contains("Test Hotel"));
            
            assertTrue(activityDesc.contains("Activity: Test Activity"));
            assertTrue(activityDesc.contains("Duration: 2 hours"));
            
            assertTrue(packageDesc.contains("Package: Test Package"));
            assertTrue(packageDesc.contains("Including flight"));
        }
        
        @Test
        @DisplayName("Categorize recommendations using pattern matching with instanceof")
        void testCategorizeRecommendations() {
            Map<String, List<Recommendation>> categorized = 
                RecommendationService.categorizeRecommendations(allRecommendations);
            
            assertEquals(4, categorized.size());
            assertEquals(1, categorized.get("Flights").size());
            assertEquals(1, categorized.get("Hotels").size());
            assertEquals(1, categorized.get("Activities").size());
            assertEquals(1, categorized.get("Packages").size());
            
            assertEquals(flight, categorized.get("Flights").getFirst());
            assertEquals(hotel, categorized.get("Hotels").getFirst());
            assertEquals(activity, categorized.get("Activities").getFirst());
            assertEquals(packageRec, categorized.get("Packages").getFirst());
        }
    }
    
    @Nested
    @DisplayName("RecommendationBox Tests")
    class RecommendationBoxTests {
        
        @Test
        @DisplayName("Create and access recommendation from box")
        void testRecommendationBox() {
            var flightBox = new RecommendationBox<>(flight);
            
            assertEquals(flight, flightBox.get());
            assertEquals(flight.getId(), flightBox.getId());
            assertTrue(flightBox.hasHighConfidence(0.7));
            assertFalse(flightBox.hasHighConfidence(0.9));
        }
        
        @Test
        @DisplayName("Map recommendation to different value")
        void testRecommendationBoxMap() {
            var hotelBox = RecommendationBox.of(hotel);
            
            String hotelName = hotelBox.map(HotelRecommendation::hotelName);
            assertEquals("Test Hotel", hotelName);
            
            double price = hotelBox.map(HotelRecommendation::pricePerNight);
            assertEquals(150.0, price);
        }
        
        @Test
        @DisplayName("Filter recommendation by confidence")
        void testRecommendationBoxFilter() {
            var activityBox = new RecommendationBox<>(activity);
            
            var filtered = activityBox.filter(0.7);
            assertTrue(filtered.isEmpty());
            
            filtered = activityBox.filter(0.5);
            assertTrue(filtered.isPresent());
            assertEquals(activity, filtered.get());
        }
        
        @Test
        @DisplayName("Cast recommendation box using pattern matching")
        void testRecommendationBoxCast() {
            // Create a box with a specific recommendation type
            RecommendationBox<Recommendation> box = new RecommendationBox<>(flight);
            
            // Try to cast to FlightRecommendation box
            var flightBoxOpt = box.castTo(FlightRecommendation.class);
            assertTrue(flightBoxOpt.isPresent());
            var flightBox = flightBoxOpt.get();
            assertEquals(flight, flightBox.get());
            
            // Try to cast to HotelRecommendation box (should fail)
            var hotelBoxOpt = box.castTo(HotelRecommendation.class);
            assertTrue(hotelBoxOpt.isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Type Erasure Tests")
    class TypeErasureTests {
        
        @Test
        @DisplayName("Demonstrate type erasure in lists")
        void testTypeErasure() {
            List<FlightRecommendation> flights = List.of(flight);
            List<HotelRecommendation> hotels = List.of(hotel);
            
            // Both are instances of List
            assertTrue(flights instanceof List);
            assertTrue(hotels instanceof List);
            
            // Both are instances of List<?>
            assertTrue(flights instanceof List<?>);
            assertTrue(hotels instanceof List<?>);
            
            // Test the isRecommendationList method
            assertTrue(RecommendationService.isRecommendationList(flights));
            assertTrue(RecommendationService.isRecommendationList(hotels));
            assertFalse(RecommendationService.isRecommendationList("not a list"));
        }
    }
}