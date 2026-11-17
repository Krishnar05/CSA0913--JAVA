# Task 12: Day-4 Design Challenge — System Adaptation and Re-evaluation

## 1. Context and Changed Requirements
The university expands the campus parking infrastructure under the following four new constraints:
1. **New Category 1 — E-Bikes:** Charged a flat fee (e.g., Rs 15.0 flat regardless of duration).
2. **New Category 2 — Delivery Vans:** Time-of-day dynamic surge pricing: charged at **double the standard car rate** during peak business hours (9:00 AM – 5:00 PM / 09:00–17:00), i.e., $2 \times \text{Rs } 30 = \text{Rs } 60.0/\text{hr}$, and standard car rate ($\text{Rs } 30.0/\text{hr}$) off-peak.
3. **One-Day Pass for Visitors:** Visitors may purchase a 24-hour pass offering unlimited parking for a fixed flat rate (e.g., Rs 150.0).
4. **Facility Structural Extension:** Expanded from 3 to 5 floors, with differing slot capacities on each floor (Floor 0: 20, Floor 1: 30, Floor 2: 40, Floor 3: 50, Floor 4: 60).

---

## 2. Section A: Impact Analysis (2 Marks)

### What Has Changed:
- Pricing is no longer purely static duration $\times$ constant hourly rate. It now requires **time-of-day awareness** (delivery vans) and **flat duration-independent rules** (e-bikes, one-day passes).
- Spatial capacity expands by 66% with heterogeneous floor capacities.

### Affected Classes and Structures:
- **`Vehicle` hierarchy:** Requires new subclasses `EBike` and `DeliveryVan`.
- **Pricing Logic:** `calculateFee` must support entry/exit timestamps or peak-hour evaluation instead of pure duration integer arithmetic.
- **`ParkingFacility`:** Must scale to 5 floors. Because our original design adopted a **Jagged Array** (`String[][]` / `Vehicle[][]`), zero code modifications are required for the array structure—only the configuration parameter `int[] slotsPerFloor` expands from length 3 to length 5.

### Classes Requiring Modification vs. Extension:
- **Classes Modified:** `Vehicle` fee contract can be extended to accept an entry-time context, or a `PricingStrategy` abstraction can be introduced.
- **Classes Extended:** `EBike` extends `Vehicle`, `DeliveryVan` extends `Car`, `OneDayPass` implements `Pass`.

---

## 3. Section B: Evaluation of the Original Design (2 Marks)

| Evaluation Criterion | Original Design Behavior | Verdict under New Requirements |
| :--- | :--- | :--- |
| **Adding New Vehicle Types** | Achieved cleanly by subclassing `Vehicle`. | **Strong:** `EBike` and `DeliveryVan` are modeled with zero impact on `Car` or `TwoWheeler`. |
| **Jagged Array Scalability** | Handled natively via dynamic row dimensions. | **Perfect:** 5 floors with differing slots instantiated seamlessly via `new int[]{20, 30, 40, 50, 60}`. |
| **Time-of-Day Pricing** | Assumed static rate per started hour. | **Requires Enhancement:** Original `calculateFee(int minutes)` lacked entry hour context. |
| **Regression Safety** | Existing tested classes (`TwoWheeler`, `Car`) remain intact. | **Adheres to Open-Closed Principle (OCP)**. |

---

## 4. Section C: Adaptation Decision and Implementation (2 Marks)

### Decision: **MODIFY (Targeted Evolution)**
Rather than executing a risky, full architectural rewrite (REDESIGN) or leaving the code unable to handle peak hours (RETAIN), we execute a **targeted backward-compatible modification**:
1. **Pass Abstraction:** Introduce `Pass` interface with `OneDayPass` and `MonthlyPass`.
2. **Time-Aware Overloading:** Overload `calculateFee(int entryHour, int durationMinutes)` while maintaining backward-compatible `calculateFee(int durationMinutes)`.
3. **Subclasses:**
   - `EBike`: Overrides `calculateFee(...)` to return flat Rs 15.0.
   - `DeliveryVan`: Overrides `calculateFee(int entryHour, int durationMinutes)` to evaluate whether entry falls between 9 AM and 5 PM.

---

## 5. Section D: Evidence-Based Defense (2 Marks)

1. **Minimal Code Churn:** Existing classes (`TwoWheeler`, `Car`, `ElectricCar`) were **not modified**. They continue to pass all baseline regression tests.
2. **Future Extensibility:** Future categories (e.g., Autonomous Shuttles, Heavy Trucks) can be introduced simply by subclassing `Vehicle` and specifying rates or policies.
3. **Array Efficiency:** The jagged array handles the 5-floor layout with 200 total slots using exactly $O(1)$ coordinate access and zero wasted array elements.
4. **Architectural Trade-Off:** Introducing time-aware pricing as an overloaded method preserves existing client contracts without breaking legacy calls.
