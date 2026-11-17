# Task 11: Implementation, Verification, Testing, and Architectural Defense

## 1. Test Suite Coverage and Scenarios

The test framework evaluates the system across five rigorous dimensions:
1. **Normal Operation:** Multi-tier entry, slot allocation, and fee processing for `TwoWheeler`, `Car`, and `ElectricCar`.
2. **Boundary Cases:**
   - Allocation of the final available slot in the facility.
   - Facility Full rejection (attempting allocation when occupancy is 100%).
   - Zero-minute duration stay (enforces minimum 1-hour charge).
   - Exact fee slab boundaries (e.g., 60.0 mins billed as 1 hour; 60.1 mins billed as 2 hours).
3. **Invalid & Edge Cases:**
   - Duplicate vehicle entry rejection (preventing double-entry for an already parked vehicle).
   - Releasing an already empty slot (returns `false`).
   - Releasing an out-of-bounds slot index (e.g., floor 5, slot 0 in a 2-floor facility).
4. **Reconciled Hand-Calculated Financial Audit:** Comparison of theoretical manual accounting vs. program execution.
5. **Architectural Defense:** Detailed evaluation of OOP pillars and identified architectural limitation.

---

## 2. Hand-Calculated Financial Reconciliation

### Audit Scenario:
- **Vehicle 1 (TwoWheeler - `TN01AA1111`):** Parked for 75 minutes.
  - Billable hours: $\lceil 75 / 60 \rceil = 2$ hours.
  - Rate: Rs 10.0/hr $\rightarrow 2 \times 10 = \mathbf{Rs\ 20.0}$.
- **Vehicle 2 (Car - `TN02BB2222`):** Parked for 60 minutes (exact boundary).
  - Billable hours: $\lceil 60 / 60 \rceil = 1$ hour.
  - Rate: Rs 30.0/hr $\rightarrow 1 \times 30 = \mathbf{Rs\ 30.0}$.
- **Vehicle 3 (ElectricCar - `TN03CC3333`):** Parked for 130 minutes + 15 kWh charging.
  - Billable hours: $\lceil 130 / 60 \rceil = 3$ hours.
  - Parking Rate: Rs 30.0/hr $\rightarrow 3 \times 30 = \text{Rs } 90.0$.
  - Charging Fee: $15 \text{ kWh} \times \text{Rs } 12.0 = \text{Rs } 180.0$.
  - Total for EV: $90.0 + 180.0 = \mathbf{Rs\ 270.0}$.
- **Vehicle 4 (Student Pass Holder Car - `TN04DD4444`):** Parked for 180 minutes.
  - Monthly pass exemption $\rightarrow \mathbf{Rs\ 0.0}$.

$$\text{Total Expected Daily Collection} = 20.0 + 30.0 + 270.0 + 0.0 = \mathbf{Rs\ 320.0}$$
**Program Execution Output:** $\mathbf{Rs\ 320.0}$ (100% Exact Reconciliation).

---

## 3. Test Results Verification Table

| Test Case ID | Test Category | Description / Inputs | Expected Output | Actual Output | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Normal | TwoWheeler entry and allocation | Assigned `{0, 0}` | Assigned `{0, 0}` | **PASS** |
| **TC-02** | Normal | Car entry and allocation | Assigned `{0, 1}` | Assigned `{0, 1}` | **PASS** |
| **TC-03** | Normal | ElectricCar entry and allocation | Assigned `{1, 0}` | Assigned `{1, 0}` | **PASS** |
| **TC-04** | Boundary | Last remaining slot allocation | Assigned `{1, 1}` | Assigned `{1, 1}` | **PASS** |
| **TC-05** | Boundary | Facility full (attempting 5th vehicle) | `null` (Facility full) | `null` | **PASS** |
| **TC-06** | Boundary | Zero-minute parking stay | Charged for 1 hour | 1 hour billed | **PASS** |
| **TC-07** | Boundary | Exact slab boundary (60 mins) | Billed 1 hour | 1 hour billed | **PASS** |
| **TC-08** | Boundary | Fractional slab boundary (61 mins) | Billed 2 hours | 2 hours billed | **PASS** |
| **TC-09** | Invalid | Duplicate vehicle entry | Rejected (`false` / `null`) | Rejected | **PASS** |
| **TC-10** | Invalid | Releasing already free slot | `false` | `false` | **PASS** |
| **TC-11** | Invalid | Releasing invalid coordinates `{5, 0}` | `false` | `false` | **PASS** |
| **TC-12** | Financial | Hand-calculated scenario audit | Rs 320.0 | Rs 320.0 | **PASS** |

---

## 4. Architectural Defense of OOP Principles

1. **Encapsulation:**
   - Internal parking matrices, counters, and vehicle plate strings are strictly `private`.
   - Invariants are enforced via validating getters/setters and immutable `final` identifiers.
2. **Inheritance:**
   - Clean taxonomy with `Vehicle` base class extended by `TwoWheeler` and `Car`, with `ElectricCar` specializing `Car`.
   - Maximizes code reuse for registration identity and billing templates.
3. **Polymorphism:**
   - Dynamic method dispatch resolves vehicle rates and charging fees at runtime through a uniform `Vehicle` array.
   - Eliminates fragile `instanceof` inspection loops.
4. **Abstraction:**
   - Clean separation of interface capabilities (`Chargeable`) from domain entity hierarchy (`Vehicle`).

### Architectural Limitation & Trade-Off:
- **Identified Limitation:** In-memory synchronization and array-based lookups are inherently single-node and non-thread-safe. In a multi-gate concurrent deployment with concurrent arrivals at North Gate and South Gate, race conditions could cause double-booking of the same slot unless explicit thread locks or atomic structures are introduced.

---

## 5. Alternative Concept and Complexity Analysis

### Storage of Active Tickets: Array vs. `ArrayList` vs. `HashMap`

| Metric | Fixed Array `ParkingTicket[N]` | Dynamic `ArrayList<ParkingTicket>` | Hash Map `HashMap<String, ParkingTicket>` |
| :--- | :--- | :--- | :--- |
| **Exit Ticket Lookup by RegNo** | **$O(n)$** linear search | **$O(n)$** linear search | **$O(1)$** average constant time |
| **Space Overhead** | **Lowest:** Contiguous pointers | **Low:** Dynamic array buffer with 1.5x amortized growth | **Higher:** Node wrappers, bucket tables, hash entry pointers |
| **Slot Release Efficiency** | $O(1)$ if coordinate known | $O(n)$ to shift elements on delete | $O(1)$ remove by key |
