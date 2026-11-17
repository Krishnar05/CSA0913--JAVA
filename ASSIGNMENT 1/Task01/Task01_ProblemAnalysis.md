# Task 1: Problem Analysis and Candidate Object Modeling

## 1. System Overview
The Campus Smart-Parking System models a multi-level university parking facility serving students, faculty/staff, and campus visitors. The facility manages entry and exit of two-wheelers, conventional cars, and electric vehicles (EVs), with optional EV charging services. It automates slot allocation, ticket issuance, duration calculation, tiered fee computation, and slot release.

---

## 2. System Inputs and Outputs

| Category | Input / Output | Description |
| :--- | :--- | :--- |
| **Inputs** | Registration Number (`regNo`) | Unique alphanumeric identifier of vehicle (e.g., `TN01AB1111`) |
| | Vehicle Category | Two-Wheeler, Car, Electric Vehicle (EV) |
| | User Role | Student, Staff, Visitor |
| | Pass Status | Monthly Pass Holder (`true` / `false`) |
| | EV Charging Request | Whether charging slot and electricity are requested |
| | Energy Consumed | Measured kilowatt-hours (kWh) for EV charging |
| | Entry & Exit Timestamps | Time vehicle entered and departed |
| **Outputs** | Allocated Slot ID | Specific `{floor, slot}` index assigned to vehicle |
| | Parking Ticket | Printed/digital ticket with unique ID, timestamps, vehicle metadata |
| | Release Confirmation | Status indicating slot successfully vacated |
| | Itemized Bill / Fee | Itemized breakdown: Parking Fee + Charging Fee (Rs.) |

---

## 3. Noun-Verb Analysis

### Nouns to Candidate Classes

| Noun in Problem | Candidate Class | Justification & Responsibility |
| :--- | :--- | :--- |
| **Parking Facility** | `ParkingFacility` | Orchestrates the entire multi-floor parking operation, slot search, and capacity monitoring. |
| **Floor** | `Floor` | Manages a collection of numbered parking slots on a specific level. |
| **Slot** | `ParkingSlot` | Represents an individual parking space; tracks occupancy, vehicle parked, and charging capability. |
| **Vehicle** | `Vehicle` *(Abstract)* | Base abstraction modeling vehicle registration number and core fee calculation contract. |
| **Car / Bike / EV** | `TwoWheeler`, `Car`, `ElectricCar` | Subclasses specializing vehicle attributes, hourly rates, and charging behavior. |
| **Ticket** | `ParkingTicket` | Immutable receipt capturing entry time, vehicle reference, assigned slot, and unique serial number. |
| **User / Customer** | `User` | Models vehicle owner type (Staff, Student, Visitor) and pass validation. |
| **Fee / Bill** | `BillingSystem` / `FeeCalculator` | Encapsulates rate schedules, slab calculations, pass discounts, and EV charging additions. |

### Verbs to Candidate Methods

| Verb in Problem | Associated Class | Candidate Method |
| :--- | :--- | :--- |
| **Allocate slot** | `ParkingFacility` | `allocate(Vehicle vehicle)` / `allocate(String regNo)` |
| **Issue ticket** | `ParkingFacility` | `issueTicket(Vehicle vehicle, int floor, int slot)` |
| **Park vehicle** | `ParkingSlot` | `occupy(Vehicle vehicle)` |
| **Release slot** | `ParkingFacility` / `ParkingSlot` | `release(int floor, int slot)` |
| **Calculate fee** | `Vehicle` / `BillingSystem` | `calculateFee(int minutes)` |
| **Charge EV** | `ElectricCar` / `Chargeable` | `chargingFee(int kWh)` |
| **Validate pass** | `User` | `hasValidPass()` |

---

## 4. Responsibilities of Candidate Classes
1. **`Vehicle` (Abstract Base Class)**:
   - Holds immutable registration number.
   - Provides abstract template method `ratePerHour()` and common `calculateFee(int minutes)`.
   - Enforces equality semantics based on registration number.
2. **`TwoWheeler` & `Car`**:
   - Specializes base rate (`TwoWheeler`: Rs 10/hr; `Car`: Rs 30/hr).
3. **`ElectricCar`**:
   - Inherits from `Car`, implements `Chargeable` interface for kWh energy billing (Rs 12/kWh).
4. **`ParkingSlot`**:
   - Maintains slot status (occupied/free), vehicle occupant reference, and slot properties.
5. **`ParkingFacility`**:
   - Tracks multidimensional floor/slot layout.
   - Allocates nearest available free slot.
   - Releases slots on vehicle exit and recalculates availability.
6. **`ParkingTicket`**:
   - Tracks unique auto-incrementing ticket ID, entry timestamp, exit timestamp, and final fee.

---

## 5. Core Business Rules
1. **Single Occupancy Invariant:** A slot can never accommodate more than one vehicle simultaneously. Attempting to assign an occupied slot must fail.
2. **Ticket Non-reusability:** Ticket serial numbers are strictly unique and monotonically increasing; a closed ticket can never be re-issued or modified.
3. **Billing Ceiling Rule:** Every started hour is charged in full (e.g., 61 minutes is billed as 2 full hours).
4. **Pass-Holder Exemption:** Valid monthly pass holders (students and staff) park free of charge ($Fee = 0$), but EV charging fees remain payable if utilized.
5. **No Negative Duration:** Exit timestamp must be strictly greater than or equal to entry timestamp.

---

## 6. Facility Assumptions
1. The facility layout can vary across floors (ragged/jagged configuration: e.g., floor 0 may have 2 slots while floor 1 has 1 slot, or 5 floors with varying capacities).
2. All vehicles enter through access gates where ticket validation and slot assignment happen sequentially.
3. Electric vehicles can park in standard car slots or designated charging slots.

---

## 7. Alternative Concept and Complexity Analysis

### Comparison: Parallel Arrays vs. Object-Oriented Domain Model
Suppose vehicle records are stored using parallel arrays instead of object instances:
```java
String[] regNo = new String[MAX];
String[] type = new String[MAX];
long[] entryTime = new long[MAX];
```

#### 1. Search by Registration Number:
- **Parallel Arrays:** Requires sequential search through `regNo[]`. Time Complexity: **$O(n)$**.
- **Object-Oriented Design:** Array of objects `Vehicle[] vehicles` also performs a sequential scan: **$O(n)$**. (Or **$O(1)$** if indexed with a `HashMap<String, Vehicle>`).
- **Critical Difference:** In parallel arrays, synchronization between arrays is brittle; sorting or filtering one array corrupts indices across all other arrays. The OO model maintains atomic data cohesion and reference integrity.

#### 2. Adding a New Vehicle Attribute (e.g., `batteryCapacityKWh`):
- **Parallel Arrays:** Requires declaring and managing a new array `double[] batteryCapacity = new double[MAX]`, updating every insertion/deletion routine, and passing four separate arrays across method signatures.
- **Object-Oriented Design:** Subclass `ElectricCar` extends `Car` with the new field `batteryCapacityKWh` without modifying existing classes or method signatures for `Car` or `TwoWheeler` (adhering to Open-Closed Principle).

#### 3. Space Complexity Analysis:
- **Parallel Arrays:** Uses contiguous primitive arrays. Memory per vehicle $\approx 3$ references/primitives ($\approx 24$ bytes + string characters).
- **Object-Oriented Design:** Each vehicle object has object header overhead (12–16 bytes on 64-bit JVM) + reference fields ($\approx 32\text{--}48$ bytes). While OO incurs modest object header overhead, it guarantees type safety, maintainability, encapsulation, and prevents state corruption.
