# Task 2: Data Representation and Precision Analysis

## 1. Data Type Selection Rationale

| Field | Java Data Type | Technical Justification |
| :--- | :--- | :--- |
| **Floor & Slot Number** | `int` | Represents non-negative discrete integer indexes; fits natural array indexing bounds without memory bloat. |
| **Vehicle Registration Number** | `String` | Alphanumeric representation with state codes, district codes, and serial numbers (e.g., `"TN01AB1111"`). Immutable and provides built-in equality and hashing support. |
| **Entry & Exit Timestamps** | `int` / `long` / `java.time.LocalDateTime` | Stored as total elapsed minutes or epoch timestamps for exact mathematical duration subtraction without date-math discrepancies. |
| **Duration** | `int` | Duration in minutes. `int` accommodates durations up to $2 \times 10^9$ minutes (thousands of years), perfectly adequate for campus parking. |
| **Parking & Charging Fee** | `double` | Monetary calculations involving fractional hours, tiered hourly rates, and energy billing per kWh (Rs 12.0/kWh). |

---

## 2. Floor and Slot Representation: Array Architecture

### Evaluation of Alternatives
1. **One-Dimensional (1D) Flat Array:**
   - Indexing formula: $\text{index} = \text{floor} \times \text{slotsPerFloor} + \text{slot}$.
   - **Limitation:** Assumes every floor has identical capacity ($\text{slotsPerFloor}$). Real campus facilities frequently feature varying capacities across floors (e.g., ground floor reserved for administrative parking or ramp restrictions).
2. **Two-Dimensional (2D) Rectangular Array `String[floors][slotsPerFloor]`:**
   - Suffers from the same rigidity as 1D flat arrays: wastes memory if floors have unequal slot capacities.
3. **Jagged (Ragged) Array `String[][] slots`:**
   - `slots = new String[slotsPerFloor.length][]` where each floor subarray `slots[f] = new String[slotsPerFloor[f]]` has an independent dimension.
   - **Decision:** **Jagged Array** is chosen because it natively supports heterogeneous floor dimensions (e.g., Floor 0: 20 slots, Floor 1: 50 slots, Floor 2: 100 slots) with zero memory wastage and direct $O(1)$ coordinate access via `slots[floor][slot]`.

---

## 3. Precision Loss and Casting Risks

### The Integer Division Bug
A common programming pitfall in billing software occurs when converting minutes to hours using integer division:
```java
int minutes = 65;
int hours = minutes / 60; // Evaluates to 1 due to integer truncation!
double fee = hours * 30.0; // Bills Rs 30.0 instead of Rs 60.0 -> LOSS OF REVENUE
```
Because the business rule states: *"Every started hour is charged in full"*, 65 minutes must be billed as 2 full hours.

### Resolution & Correct Arithmetic
1. **Using Ceiling with Floating-Point Promotion:**
   ```java
   int hours = (int) Math.ceil(minutes / 60.0);
   ```
2. **Pure Integer Ceiling Arithmetic (Prevents floating-point rounding errors):**
   ```java
   int hours = (minutes + 59) / 60;
   ```
   For $minutes = 60 \rightarrow (60 + 59)/60 = 119/60 = 1$ hour.
   For $minutes = 61 \rightarrow (61 + 59)/60 = 120/60 = 2$ hours.
   This guarantees precision without floating-point conversion pitfalls.

---

## 4. Input Handling: Scanner vs. Command-Line Arguments
- **Interactive Console / Testing:** `java.util.Scanner` reads dynamic interactive commands at runtime.
- **Batch Processing / Automated Testing:** `String[] args` provides command-line automation without blocking user prompts.

---

## 5. Alternative Concept and Complexity Analysis

| Metric | Jagged Array `String[F][S_f]` | Flat 1D Array `String[F * S]` | Dynamic `ArrayList<String[]>` |
| :--- | :--- | :--- | :--- |
| **Lookup Time** | **$O(1)$** (`slots[f][s]`) | **$O(1)$** (if uniform) / $O(F)$ lookup | **$O(1)$** (`list.get(f)[s]`) |
| **Ragged Floors Support** | **Native** | Requires prefix sum offset array | **Native** |
| **Memory Overhead** | $F$ pointer references + total slots | Minimal (single array header) | List object + node wrappers |
| **Resizing Flexibility** | Moderate (replace floor array) | Hard ($O(N)$ reallocation) | Easy (`add()` / `remove()`) |

When floor slot counts vary, the 1D flat array indexing formula $\text{index} = \text{floor} \times \text{slotsPerFloor} + \text{slot}$ breaks unless an auxiliary prefix-sum array `floorOffsets[]` is maintained. The jagged array provides optimal cache locality and clean coordinate mapping.
