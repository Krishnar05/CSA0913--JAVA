# Task 3: Control Logic Design and Implementation

## 1. Control Logic Analysis

### 1.1 Searching Floors and Slots
The allocation algorithm searches floors hierarchically from Ground Floor (index 0) upwards, examining slot 0 to slot $S_f - 1$ on each floor.
- **Search Termination:** The search terminates immediately via an early `return new int[]{f, s}` the instant an unoccupied slot (`slots[f][s] == null`) is found.
- **Exhaustion (Full Capacity):** If all nested loops complete without finding an empty slot, the method returns `null` to signal that the parking facility is fully occupied.

### 1.2 Fee Slabs and Branching
Fee calculations incorporate tiered pricing models:
- **Pass Holders:** `if (isPassHolder) return 0.0;`
- **Hourly Slabs:** Can be evaluated via conditional chains (`if-else if-else`) or ceiling multipliers:
  - First hour flat rate.
  - Subsequent hours at per-hour rate.
  - Daily cap if duration exceeds 8 hours.

### 1.3 Use of Jump Statements (`return`, `break`, `continue`)
- `return`: Eliminates unnecessary iterations immediately upon locating an eligible slot.
- `break`: Breaks out of inner slot loop when a specific floor condition is satisfied.
- `continue`: Skips reserved or out-of-service slots.

---

## 2. SIMATS Engineering Standard Algorithmic Pseudocode

### Algorithm 1: ALLOCATE_SLOT
```text
ALGORITHM AllocateSlot(slots, vehicleNo)
INPUT: slots as Jagged Array of Strings, vehicleNo as String
OUTPUT: Array of two integers [floor, slot] or NULL

BEGIN
    IF vehicleNo IS NULL OR vehicleNo IS EMPTY THEN
        RETURN NULL
    END IF

    FOR f ← 0 TO LENGTH(slots) - 1 DO
        FOR s ← 0 TO LENGTH(slots[f]) - 1 DO
            IF slots[f][s] IS NULL THEN
                slots[f][s] ← vehicleNo
                RETURN [f, s]
            END IF
        END FOR
    END FOR

    RETURN NULL
END
```

### Algorithm 2: CALCULATE_PARKING_FEE
```text
ALGORITHM CalculateParkingFee(vehicleType, durationMinutes, isPassHolder)
INPUT: vehicleType as String, durationMinutes as Integer, isPassHolder as Boolean
OUTPUT: totalFee as Real

BEGIN
    IF isPassHolder IS TRUE THEN
        RETURN 0.0
    END IF

    IF durationMinutes <= 0 THEN
        RETURN 0.0
    END IF

    // Ceiling of duration to billable hours using integer arithmetic
    billableHours ← (durationMinutes + 59) / 60

    CASE vehicleType OF
        "TWOWHEELER":
            ratePerHour ← 10.0
        "CAR":
            ratePerHour ← 30.0
        "ELECTRICCAR":
            ratePerHour ← 30.0
        DEFAULT:
            ratePerHour ← 20.0
    END CASE

    totalFee ← billableHours * ratePerHour
    RETURN totalFee
END
```

---

## 3. Alternative Concept and Complexity Analysis

### Comparison: Linear Scan vs. Free Slot Stack Architecture

| Metric | Current Solution (Nested Linear Scan) | Alternative Solution (Free Slot Stack / Queue) |
| :--- | :--- | :--- |
| **`allocate()` Time Complexity** | **$O(F \times S)$** worst-case scan | **$O(1)$** pop from free-slots stack |
| **`release()` Time Complexity** | **$O(1)$** direct indexing validation | **$O(1)$** push slot back to free stack |
| **Extra Space Complexity** | **$O(1)$** (no auxiliary structure) | **$O(F \times S)$** stack storage holding all free coordinates |
| **Simplicity & Overhead** | Low memory footprint; optimal for small-to-medium facilities ($F \le 5, S \le 100$). | Slightly higher memory; ideal for mega-facilities with thousands of slots. |
