# Task 10: Encapsulation, Packages, and Defensive Programming

## 1. Package Architecture and Modular Organization

To uphold the Single Responsibility Principle and enforce clean architectural boundaries, classes are organized into three distinct packages:
```
parking/
  ├── model/       # Domain entities and value objects (Vehicle, ParkingSlot, ParkingTicket)
  ├── service/     # Business logic, fee computation, slot allocation (ParkingService)
  └── app/         # Application bootstrap, CLI drivers, entry point (Main)
```

---

## 2. Access Modifier Matrix and Justification

| Access Modifier | System Artifact | Justification |
| :--- | :--- | :--- |
| **`private`** | Instance fields (`regNo`, `entryTime`, `slots[][]`) | Prevents direct external state corruption; enforces strict access through validating methods. |
| **`default` (package-private)** | Internal helper routines in `parking.service` | Restricts internal helper methods and state mutators strictly within the service layer; invisible to outer app layer. |
| **`protected`** | Subclass extension methods (`ratePerHour()`) | Allows child classes in different packages to specialize behaviors while hiding them from unrelated consumers. |
| **`public`** | High-level API contracts (`allocate()`, `release()`, `calculateFee()`) | Standard public interface for client invocation. |

---

## 3. Defensive Programming: Data Validation in Setters and Getters

Public setters must guard state invariants by rejecting invalid parameters immediately:
```java
public void setDurationMinutes(int durationMinutes) {
    if (durationMinutes < 0) {
        throw new IllegalArgumentException("Parking duration cannot be negative: " + durationMinutes);
    }
    this.durationMinutes = durationMinutes;
}

public void setSlot(int floor, int slot) {
    if (floor < 0 || slot < 0) {
        throw new IndexOutOfBoundsException("Slot coordinates must be non-negative.");
    }
    this.floor = floor;
    this.slot = slot;
}
```

---

## 4. Wrapper Classes and Autoboxing Hazards

### The Integer Cache Comparison Bug (`==` vs `.equals()`)
Java autoboxes primitive `int` to `Integer` via `Integer.valueOf(int)`.
- The JVM caches `Integer` instances in the range **$[-128, 127]$**.
- In this range, reference equality `==` happens to evaluate to `true`.
- **The Pitfall:** If ticket IDs or slot numbers exceed 127:
  ```java
  Integer id1 = 1000;
  Integer id2 = 1000;
  boolean bug = (id1 == id2); // FALSE! Independent heap objects compared by reference
  boolean fix = id1.equals(id2); // TRUE! Compared by value
  ```
- **Resolution:** In our design, primitive types (`int`, `long`, `double`) are used for numeric identifiers and arithmetic, preventing autoboxing overhead and identity comparison bugs. Where wrapper objects are unavoidable, `.equals()` is strictly enforced.

---

## 5. Alternative Concept and Complexity Analysis

### Encapsulation with Validated Setters vs. Public Fields with Call-Site Validation

Suppose all fields were `public` without setters, requiring the calling code to perform validation:
```java
// Vulnerable Public Field Model:
ticket.durationMinutes = userInput; // Calling code must remember to validate!
```

### Consequences:
1. **Redundancy & DRY Violation:**
   - The validation rule `if (duration < 0)` must be repeated in every controller, CLI handler, API endpoint, and automated test suite.
   - For $M$ call sites and $N$ updates, the check is duplicated across $M \times N$ locations.
2. **State Corruption Risk:**
   - If even a single developer omits the validation check, corrupted state (e.g., negative duration resulting in negative billing where the university pays the visitor!) enters the system.
3. **Execution Cost:**
   - Centralized validation inside setters costs **$O(1)$** per update, identical to call-site validation in runtime execution, but reduces defect likelihood and maintenance overhead to zero.
