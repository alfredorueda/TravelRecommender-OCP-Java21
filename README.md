# TravelRecommender-OCP-Java21

A hands-on Maven project designed for engineers preparing for the **OCP Java 21 Certification**. This repository uses a realistic travel recommendation engine to master Java generics and sealed types.

## ✨ Highlights

- Uses modern Java 21 features:
  - Sealed types and generic records
  - Pattern matching for switch/instanceof
  - Type-safe wildcards (`?`, `? extends`, `? super`)
- Code comments explain certification-relevant concepts
- Perfect for advanced learners and Java instructors

## 📚 Topics Covered

- ✅ Bounded wildcards and PECS (Producer Extends, Consumer Super)
- ✅ Sealed type hierarchies for controlled extensibility
- ✅ Record-based generic data models
- ✅ Type erasure limitations
- ✅ Pattern matching for branching logic
- ✅ Generic return types and methods

## 🚀 How to Use

```bash
# Clone the repository
git clone https://github.com/yourusername/TravelRecommender-OCP-Java21.git

# Build the project
mvn clean install

# Run the tests
mvn test

# Run the demo
mvn exec:java -Dexec.mainClass="com.edreams.travelrecommender.TravelRecommendationDemo"
```

## 💡 Project Structure

- **Model Package**: Contains sealed interface `Recommendation` and its permitted implementations:
  - `FlightRecommendation`: Record for flight details
  - `HotelRecommendation`: Record for hotel details
  - `ActivityRecommendation`: Record for activity details
  - `PackageRecommendation`: Record for combined travel packages
  - `RecommendationBox<T>`: Generic wrapper demonstrating bounded type parameters

- **Service Class**: `RecommendationService` demonstrates:
  - Generic methods with bounded wildcards
  - PECS principle implementation
  - Pattern matching for switch
  - Type erasure considerations

- **Demo Class**: `TravelRecommendationDemo` shows practical usage of all features

- **Test Class**: Comprehensive tests demonstrating how to test generic components

## 🧠 Key Java 21 Features Demonstrated

### Sealed Types

```java
public sealed interface Recommendation 
    permits FlightRecommendation, HotelRecommendation, ActivityRecommendation, PackageRecommendation {
    // ...
}
```

### Generic Records

```java
public record FlightRecommendation(
    String id,
    String title,
    // other fields...
) implements Recommendation {
    // ...
}
```

### Pattern Matching for Switch

```java
return switch (recommendation) {
    case FlightRecommendation flight -> 
        // Use flight-specific methods and fields
    case HotelRecommendation hotel -> 
        // Use hotel-specific methods and fields
    // ...
};
```

### Pattern Matching for instanceof

```java
if (recommendation instanceof FlightRecommendation flight) {
    // Can directly use flight without casting
    String airline = flight.airline();
}
```

### PECS Principle

```java
// Producer extends (read from)
public static <T extends Recommendation> List<T> filterByPredicate(
        List<? extends T> recommendations, 
        Predicate<? super T> predicate) {
    // ...
}

// Consumer super (write to)
public static <T extends Recommendation> void addRecommendation(
        List<? super T> recommendations, 
        T recommendation) {
    // ...
}
```

## 👨‍🏫 Educational Purpose

This project is designed to assist instructors in teaching high-level Java features with meaningful, real-world scenarios—like those used at eDreams. Each class and method contains detailed Javadoc comments explaining the Java 21 concepts being demonstrated.

## 📝 License

MIT