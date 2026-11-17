# Task 7: Method Overriding, Object Contracts, and Immutability

## 1. Method Overriding in the Vehicle Hierarchy

### 1.1 Specialized Subclass Behavior
- **`ratePerHour()`:** The abstract superclass `Vehicle` mandates `protected abstract double ratePerHour()`. Each subclass overrides this method to return its domain-specific tariff:
  - `TwoWheeler`: returns Rs 10.0/hr
  - `Car`: returns Rs 30.0/hr
  - `ElectricCar`: inherits Rs 30.0/hr base parking and adds kWh charging tariffs via `Chargeable`.

### 1.2 The `@Override` Annotation and Overriding Rules
1. **Method Signature:** Must match the superclass method name and parameter types identically.
2. **Return Type:** Must be identical or a covariant subtype.
3. **Access Privileges:** Cannot be made more restrictive (e.g., `protected` in superclass cannot be reduced to `private` or default in subclass; expanding to `public` is permitted).
4. **Checked Exceptions:** Subclass cannot declare broader or new checked exceptions.
5. **Compiler Safety:** `@Override` informs the javac compiler to assert that a corresponding superclass method is indeed overridden, immediately catching typos (such as `rateperhour()`).

---

## 2. Strategic Use of the `final` Keyword

1. **`final` Variable (`private final String regNo`):**
   - Guarantees immutability. Once a vehicle's license plate is established at object instantiation, it cannot be tampered with or modified.
2. **`final` Method (`public final double calculateFee(int minutes)`):**
   - Protects core corporate billing algorithms. Ensures that rogue subclasses cannot tamper with the mandatory rounding ceiling arithmetic.
3. **`final` Class:**
   - Prevents subclassing when a class design is complete and closed against extension (e.g., security-critical or value objects).

---

## 3. Overriding `equals()`, `hashCode()`, and `toString()`

### 3.1 The Identity Equality Problem
By default, `java.lang.Object.equals(Object o)` evaluates **reference identity** (`this == o`). Thus, two independent `Vehicle` instances modeling the exact same car (`TN02B2`) would be evaluated as unequal.

### 3.2 Canonical Implementation
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Vehicle)) return false;
    Vehicle other = (Vehicle) o;
    return regNo != null && regNo.equals(other.regNo);
}

@Override
public int hashCode() {
    return regNo != null ? regNo.hashCode() : 0;
}

@Override
public String toString() {
    return getClass().getSimpleName() + " [Plate=" + regNo + "]";
}
```
- **Equivalence Contract:** Satisfies reflexivity, symmetry, transitivity, and consistency.
- **`hashCode` Contract:** If `v1.equals(v2)` is `true`, then `v1.hashCode() == v2.hashCode()` is guaranteed, preventing catastrophic silent bugs in hash collections (`HashSet`, `HashMap`).

---

## 4. Alternative Concept and Complexity Analysis

### Dynamic Overriding vs. Monolithic Switch Statement

Suppose a single non-overridden `calculateFee(String type, int minutes)` method used a `switch(type)`:

```java
public double calculateFee(String type, int minutes) {
    int hours = (minutes + 59) / 60;
    switch(type) {
        case "TWOWHEELER": return hours * 10;
        case "CAR": return hours * 30;
        case "ELECTRIC_CAR": return hours * 30 + charging;
        // Requires modifying this method every time a new vehicle type is added!
    }
}
```

| Factor | Method Overriding (Polymorphism) | Centralized Switch Statement |
| :--- | :--- | :--- |
| **Open-Closed Principle (OCP)** | **Adhered to:** Add new vehicle by creating new subclass without touching existing classes. | **Violated:** Every new category requires opening and modifying existing, tested classes. |
| **Time Complexity** | **$O(1)$** via single vtable pointer offset. | **$O(1)$** table switch or **$O(k)$** string branch comparisons. |
| **Maintenance Impact** | 0 existing files modified; 1 new file added. | Existing core service file modified; high regression risk. |
