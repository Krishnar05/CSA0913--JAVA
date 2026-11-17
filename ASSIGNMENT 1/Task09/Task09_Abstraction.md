# Task 9: Abstraction — Abstract Classes and Interface Modeling

## 1. Abstract Classes vs. Interfaces in the System

### 1.1 Abstract Class: `Vehicle`
- **Definition:** Represents a core identity abstraction ("is-a" relationship).
- **Abstract Method:** `protected abstract double ratePerHour();`
  - Forces every concrete vehicle subclass to define its own base tariff schedule without dictating the tariff value at the root.
- **Concrete Reusable Methods:** `calculateFee(int minutes)` provides non-negotiable common rounding logic (`(minutes + 59) / 60 * ratePerHour()`).
- **Encapsulated State:** Holds `private final String regNo` and the constructor `Vehicle(String regNo)`.

### 1.2 Interfaces: Capabilities and Behaviors ("can-do")
- **`Chargeable` Interface:**
  - Capabilities: `double chargingFee(int kWh);`
  - Not all vehicles can charge. Modeling `Chargeable` as an interface permits selective capability attachment to `ElectricCar` (and future EV trucks, e-bikes) without polluting non-electric vehicles (`Car`, `TwoWheeler`).
- **`PassHolder` / `Discountable` Interface:**
  - Capabilities: `boolean hasValidPass();` and `double applyDiscount(double rawFee);`

### 1.3 Architectural Criteria for Choosing Between Abstract Class and Interface

| Decision Factor | Use Abstract Class (`Vehicle`) | Use Interface (`Chargeable`) |
| :--- | :--- | :--- |
| **Relationship** | Strong ontological identity ("is-a"). | Behavioral capability or contract ("can-do"). |
| **State / Fields** | Can maintain private state, instance fields (`regNo`), and constructor invariants. | Strictly stateless (can only declare `public static final` constants). |
| **Inheritance** | Single inheritance (a class can extend only one abstract class). | Multiple realization (a class can implement multiple interfaces). |
| **Code Evolution** | Can add non-abstract methods with full state access. | Default methods provide behavior without state access. |

---

## 2. Advanced Interface Features: Default and Static Methods

### 2.1 Default Methods in Interfaces
Provide optional default behavior or backward compatibility without forcing all implementing classes to provide an implementation:
```java
interface Chargeable {
    double chargingFee(int kWh);

    // Default method providing smart charging notification
    default String getChargingStatus(int kWh) {
        return "Delivered " + kWh + " kWh at standard rate.";
    }
}
```

### 2.2 Static Utility Methods in Interfaces
Provide utility functions directly associated with the interface contract:
```java
interface Chargeable {
    static double getBaselineKwhRate() {
        return 12.0; // Centralized rate lookup
    }
}
```

---

## 3. Alternative Concept and Complexity Analysis

### Inverting the Roles: `Vehicle` as Interface & `Chargeable` as Abstract Class

Suppose `Vehicle` were converted to an interface with default methods, and `Chargeable` to an abstract class:
```java
interface Vehicle {
    String getRegNo();
    default double calculateFee(int minutes) { ... }
}
abstract class Chargeable {
    abstract double chargingFee(int kWh);
}
```

### Consequences:
1. **Loss of State & Constructor Enforcement:**
   - Interfaces cannot hold instance variables (`regNo`). Every subclass (`Car`, `TwoWheeler`) would be forced to duplicate the field declaration `private final String regNo;`, getter, and equals/hashCode boilerplate.
2. **Single Inheritance Limitation for EVs:**
   - Because Java permits extending only **one** class, if `Chargeable` were an abstract class, then an `ElectricCar` could not extend both `Car` and `Chargeable`! Java's single class inheritance prevents this entirely unless multiple levels are contorted.
3. **Execution Time & Memory:**
   - Method calls on interfaces dispatch via `invokeinterface` (which requires dynamic table lookup across class definitions) compared to `invokevirtual` for abstract classes. While HotSpot's inline caching optimizes both, `invokevirtual` exhibits faster stabilization and tighter devirtualization.
