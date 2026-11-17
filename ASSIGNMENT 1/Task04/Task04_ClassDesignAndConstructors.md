# Task 4: Classes, Objects, Constructors, and Memory Architecture

## 1. Class Definitions & Responsibilities

### 1.1 `ParkingTicket`
- **Fields:**
  - `ticketId` (`long`): Unique sequential identifier.
  - `regNo` (`String`): Associated vehicle plate number.
  - `floor` (`int`): Allocated floor index.
  - `slot` (`int`): Allocated slot index.
  - `entryMinutes` (`int`): Entry time in minutes from baseline.
  - `isPassHolder` (`boolean`): University pass status.
- **Methods:**
  - `getTicketId()`, `getRegNo()`, `getFloor()`, `getSlot()`, `getEntryMinutes()`, `isPassHolder()`, `toString()`

### 1.2 `ParkingSlot`
- **Fields:** `floor` (`int`), `slotNumber` (`int`), `vehicle` (`Vehicle`), `isEVCharging` (`boolean`)
- **Methods:** `occupy(Vehicle v)`, `vacate()`, `isFree()`, `getVehicle()`

---

## 2. Constructor Taxonomy and Constructor Chaining

### 2.1 Default Constructor
Initializes a blank or unassigned state. For example, creating a prototype ticket with default system defaults before binding to an active vehicle.

### 2.2 Parameterized Constructor
Initializes an object with explicit arguments for all attributes, establishing invariant validity immediately upon instantiation:
```java
public ParkingTicket(long ticketId, String regNo, int floor, int slot, int entryMinutes, boolean isPassHolder) {
    this.ticketId = ticketId;
    this.regNo = regNo;
    this.floor = floor;
    this.slot = slot;
    this.entryMinutes = entryMinutes;
    this.isPassHolder = isPassHolder;
}
```

### 2.3 Overloaded Constructors & Constructor Chaining (`this(...)`)
Overloaded constructors provide convenient entry points (e.g., standard visitor tickets default `isPassHolder = false` and `entryMinutes = 0`).
To prevent code duplication, constructors chain to the canonical master constructor using `this(...)` as the very first line of code:
```java
// Overloaded constructor for quick issuance with default entry time and non-pass holder
public ParkingTicket(long ticketId, String regNo, int floor, int slot) {
    this(ticketId, regNo, floor, slot, 0, false); // Chaining
}
```

### 2.4 Copy Constructor
A copy constructor creates an independent duplicate of an existing object with identical attributes:
```java
public ParkingTicket(ParkingTicket original) {
    this(original.ticketId, original.regNo, original.floor, original.slot, original.entryMinutes, original.isPassHolder);
}
```
**Justification:** Essential when re-issuing a duplicate receipt/ticket or archiving ticket state at vehicle checkout without exposing internal mutable references.

---

## 3. JVM Memory Model: Stack vs. Heap

When executing:
```java
ParkingTicket t1 = new ParkingTicket(101L, "TN01AB1111", 0, 1, 30, false);
```
1. **Stack Memory:**
   - The reference variable `t1` (a 64-bit reference address / 32-bit with Compressed Oops) is allocated within the current thread's stack frame.
   - Primitive parameter values and local variables reside directly in the stack frame.
2. **Heap Memory:**
   - The actual `ParkingTicket` instance is dynamically allocated in Eden Space within the JVM Heap.
   - The heap object contains the object header (Mark Word + Klass Word = 12–16 bytes) and memory slots for instance fields (`ticketId`, `regNo` reference, `floor`, `slot`, etc.).
   - The `regNo` field points to a string object located in the String Constant Pool / Heap.

---

## 4. Alternative Concept and Complexity Analysis

| Criteria | Telescoping Constructors + Chaining | Default Constructor + Setters (JavaBeans) | Static Factory Methods (`of(...)`) |
| :--- | :--- | :--- | :--- |
| **Object Invariant Guarantee** | **Strong:** Object cannot exist in partially initialized state. | **Weak:** Object is instantiated incomplete; fields populated sequentially. | **Strong:** Guarantees validation before returning instance. |
| **Immutability Support** | **Full:** Allows `final` fields. | **None:** Requires public mutable setters. | **Full:** Allows `final` fields and subclass flexibility. |
| **Creation Cost for $n$ Objects** | $O(n)$ time, minimal heap overhead ($O(n)$ space). | $O(n)$ time, $n \times k$ setter stack frame calls. | $O(n)$ time, comparable minimal space. |
| **Code Maintainability** | High, localized validation in single chained target. | Error-prone; caller might forget to invoke critical setters. | High; self-documenting method names. |
