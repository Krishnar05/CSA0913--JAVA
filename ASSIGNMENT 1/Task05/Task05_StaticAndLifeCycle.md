# Task 5: Static Members, Object Passing, and Life Cycle Management

## 1. Static Variables vs. Instance Variables

### 1.1 Where Static Variables are Required
- **Global Ticket Sequence Counter (`ticketCounter`):** Must be shared across all ticket instantiations to ensure monotonically increasing, non-duplicate ticket IDs system-wide.
- **Campus Facility Master Rates / Capacity Metrics:** Facility-wide limits and shared operational parameters.

### 1.2 Why an Instance Variable Would be Wrong
If `ticketCounter` were an instance variable inside `ParkingTicket`:
- Every new `new ParkingTicket()` would allocate its own private `ticketCounter` initialized to 0, causing every ticket to receive `Ticket #1`.
- Unique identification and audit trails would be destroyed.

---

## 2. Static Methods and Static Blocks

### 2.1 Static Methods
- Utility methods that do not depend on the state of a specific instance:
  ```java
  public static long getNextTicketId() {
      return ++ticketCounter;
  }
  ```
- Validation routines, such as `public static boolean isValidRegNo(String regNo)`.

### 2.2 Static Initialization Block
Executes exactly once when the class is first loaded into JVM memory by the ClassLoader:
```java
static {
    // Perform one-time system bootstrap, rate lookup, or configuration initialization
    BASE_HOURLY_RATE = 20.0;
    System.out.println("[Static Initializer]: Campus Parking System initialized.");
}
```

---

## 3. Object Life Cycle and Garbage Collection (GC)

### 3.1 Object States
1. **Creation (Eden Generation):** Instantiated via `new ParkingTicket(...)`.
2. **Active State:** Referenced by `slots[floor][slot]` or an active tickets registry. Strongly reachable; cannot be garbage collected.
3. **Vehicle Departure:** When the vehicle exits, `release()` nullifies the reference:
   ```java
   slots[floor][slot] = null;
   ```
4. **Unreachable State:** When all local variables, ticket variables, and collection references pointing to that `ParkingTicket` fall out of scope or are set to `null`, the object becomes **eligible for Garbage Collection**.
5. **Collection (GC):** During minor or major GC pauses (e.g., G1 GC, ZGC), the JVM identifies unreferenced heap objects and reclaims their memory.

### 3.2 Object Passing and Returning in Java
- Java is strictly **pass-by-value**.
- When an object reference (e.g. `Vehicle v`) is passed to a method, the **reference value (memory pointer address)** is copied by value onto the call stack.
- Mutations to fields of the referenced object persist on the heap, but reassigning `v = null` inside the method modifies only the local stack copy, leaving the caller's reference unchanged.

---

## 4. Alternative Concept and Complexity Analysis

### Static Counter vs. Instance Counter for Multi-Gate Facilities (North Gate vs. South Gate)

| Metric | Single Static Counter `ParkingTicket.ticketCounter` | Per-Facility Instance Counter `facility.counter` | Compound ID Solution (`gateCode + "-" + counter`) |
| :--- | :--- | :--- | :--- |
| **Multi-Gate Isolation** | Single global sequence: Gate 1 gets #1, Gate 2 gets #2, Gate 1 gets #3. | Both facilities issue tickets starting at #1 $\rightarrow$ **Collision risk!** | Gate 1 issues `NG-1`, `NG-2`; Gate 2 issues `SG-1`, `SG-2` $\rightarrow$ **Collision-free!** |
| **Concurrency / Thread Contention** | High contention if multiple gates update single atomic static variable. | Zero contention between facilities. | Zero contention across facilities; clear audit origin. |
| **Memory per $k$ Facilities & $n$ Tickets** | **$8$ bytes** static memory total. | **$8 \times k$ bytes** ($O(k)$ space). | **$O(k)$ space** (counter + string prefix). |
