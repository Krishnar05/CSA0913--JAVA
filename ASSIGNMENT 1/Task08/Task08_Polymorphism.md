# Task 8: Polymorphism and Dynamic Method Dispatch

## 1. Compile-Time vs. Runtime Polymorphism

### 1.1 Compile-Time Polymorphism (Method Overloading)
- **Concept:** Multiple methods in the same class share identical names but differ in parameter count, type, or order.
- **Example:**
  ```java
  public double calculateFee(int minutes) { ... }
  public double calculateFee(LocalTime entryTime, LocalTime exitTime) { ... }
  ```
- **Compiler Resolution (Static Binding):** The javac compiler resolves method binding during compilation based strictly on the declared argument types and call signature.

### 1.2 Runtime Polymorphism (Dynamic Method Dispatch)
- **Concept:** Enables a superclass reference to invoke an overridden method, where the JVM determines at runtime which version of the method to execute based on the actual runtime object type.
- **Upcasting:** Subclass instances (`TwoWheeler`, `Car`, `ElectricCar`) are upcast into a uniform array of superclass references:
  ```java
  Vehicle[] parked = { new TwoWheeler("TN01A1"), new Car("TN02B2"), new ElectricCar("TN03C3") };
  ```
- **Virtual Method Table (vtable):** When `v.calculateFee(130)` is called:
  1. `calculateFee` invokes `ratePerHour()`.
  2. The JVM inspects the object header of the target heap object to access its `vtable`.
  3. The call dispatches to the specific subclass's `ratePerHour()` without needing conditionals.

---

## 2. Dynamic Method Dispatch Trace Table

| Call | Declared Reference Type | Actual Object Type on Heap | Dispatched Method Executed | Rate Returned | Total Fee |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `parked[0].calculateFee(130)` | `Vehicle` | `TwoWheeler` | `TwoWheeler.ratePerHour()` | Rs 10.0 | **Rs 30.0** |
| `parked[1].calculateFee(130)` | `Vehicle` | `Car` | `Car.ratePerHour()` | Rs 30.0 | **Rs 90.0** |
| `parked[2].calculateFee(130)` | `Vehicle` | `ElectricCar` | `Car.ratePerHour()` + `Chargeable.chargingFee(10)` | Rs 30.0 + (10 kWh $\times$ 12) | **Rs 210.0** |

---

## 3. Analysis: Why the Last Line Prints `true`

```java
System.out.println(new Car("TN02B2").equals(new ElectricCar("TN02B2")));
```

### Why it evaluates to `true`:
In the `equals` implementation of `Vehicle`:
```java
@Override
public boolean equals(Object o) {
    return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
}
```
1. `new ElectricCar("TN02B2")` is passed into `Car.equals(...)`.
2. The check `o instanceof Vehicle` evaluates to `true` because `ElectricCar` is a subclass of `Car`, which is a subclass of `Vehicle`.
3. The license plate `regNo` of both instances is `"TN02B2"`.
4. Therefore, the method returns `true`.

### Architectural Assessment: Is this correct behavior?
- **Domain Correctness:** **YES, for a parking facility.** In a physical parking lot, a vehicle's legal identity is defined strictly by its license plate (`regNo`). Two records with the identical license plate represent the exact same physical vehicle on campus premises, preventing duplicate ticket issuance and double-parking fraud.
- **Symmetry Consideration:** If `ElectricCar` had overridden `equals` with a stricter `getClass() == o.getClass()` check, it would violate symmetry. By anchoring equivalence to the domain key (`regNo`) across the abstract `Vehicle` root, the system correctly preserves the Liskov Substitution Principle (LSP).

---

## 4. Alternative Concept and Complexity Analysis

### Dynamic Method Dispatch vs. `instanceof` Checking Cascade

```java
// Alternative without polymorphism:
for (Vehicle v : parked) {
    double fee = 0;
    if (v instanceof TwoWheeler) {
        fee = ((TwoWheeler) v).calculateFee(130);
    } else if (v instanceof ElectricCar) {
        fee = ((ElectricCar) v).calculateFee(130);
    } else if (v instanceof Car) {
        fee = ((Car) v).calculateFee(130);
    }
}
```

| Factor | Dynamic Dispatch (Virtual Table) | `instanceof` + Downcasting Cascade |
| :--- | :--- | :--- |
| **Time Complexity per Call** | **$O(1)$** constant-time indexed table jump | **$O(k)$** sequential branch evaluations |
| **Coupling & Fragility** | Decoupled; caller interacts only with `Vehicle` | Strongly coupled; loop must know all subclasses |
| **Extensibility (OCP)** | Adding a 10th vehicle type requires **0 changes** to loop | Adding a 10th vehicle type requires **updating every loop in the codebase** |
