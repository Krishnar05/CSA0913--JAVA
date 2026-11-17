// Task 11: Complete Integrated Smart Parking System Implementation & Comprehensive Test Suite
import java.util.*;

public class SmartParkingSystem {

    public interface Chargeable {
        double chargingFee(int kWh);
    }

    public static abstract class Vehicle {
        private final String regNo;

        protected Vehicle(String regNo) {
            if (regNo == null || regNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number cannot be null or empty.");
            }
            this.regNo = regNo.trim().toUpperCase();
        }

        public String getRegNo() { return regNo; }

        protected abstract double ratePerHour();

        public double calculateFee(int minutes) {
            if (minutes < 0) throw new IllegalArgumentException("Negative duration.");
            int hours = (minutes + 59) / 60;
            if (hours == 0) hours = 1; // Minimum 1 billable hour
            return hours * ratePerHour();
        }

        @Override public boolean equals(Object o) {
            return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
        }

        @Override public int hashCode() { return regNo.hashCode(); }

        @Override public String toString() {
            return getClass().getSimpleName() + "(" + regNo + ")";
        }
    }

    public static class TwoWheeler extends Vehicle {
        public TwoWheeler(String regNo) { super(regNo); }
        @Override protected double ratePerHour() { return 10.0; }
    }

    public static class Car extends Vehicle {
        public Car(String regNo) { super(regNo); }
        @Override protected double ratePerHour() { return 30.0; }
    }

    public static class ElectricCar extends Car implements Chargeable {
        public ElectricCar(String regNo) { super(regNo); }
        @Override public double chargingFee(int kWh) { return kWh * 12.0; }
    }

    public static class ParkingFacility {
        private final Vehicle[][] slots;

        public ParkingFacility(int[] slotsPerFloor) {
            slots = new Vehicle[slotsPerFloor.length][];
            for (int f = 0; f < slotsPerFloor.length; f++) {
                slots[f] = new Vehicle[slotsPerFloor[f]];
            }
        }

        public boolean isParked(String regNo) {
            for (Vehicle[] floor : slots) {
                for (Vehicle v : floor) {
                    if (v != null && v.getRegNo().equalsIgnoreCase(regNo)) return true;
                }
            }
            return false;
        }

        public int[] allocate(Vehicle v) {
            if (isParked(v.getRegNo())) {
                System.out.println("  [Error]: Vehicle " + v.getRegNo() + " is already parked in facility!");
                return null;
            }
            for (int f = 0; f < slots.length; f++) {
                for (int s = 0; s < slots[f].length; s++) {
                    if (slots[f][s] == null) {
                        slots[f][s] = v;
                        return new int[]{f, s};
                    }
                }
            }
            return null; // Full
        }

        public boolean release(int floor, int slot) {
            if (floor < 0 || floor >= slots.length) return false;
            if (slot < 0 || slot >= slots[floor].length) return false;
            if (slots[floor][slot] == null) return false;
            slots[floor][slot] = null;
            return true;
        }

        public Vehicle getVehicle(int floor, int slot) {
            if (floor < 0 || floor >= slots.length || slot < 0 || slot >= slots[floor].length) return null;
            return slots[floor][slot];
        }
    }

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("   CAMPUS SMART-PARKING SYSTEM: INTEGRATED TEST SUITE       ");
        System.out.println("============================================================");

        // Facility layout: Floor 0 (2 slots), Floor 1 (2 slots) -> Total capacity = 4
        ParkingFacility facility = new ParkingFacility(new int[]{2, 2});

        // 1. Normal Operations
        System.out.println("\n--- 1. Normal Allocations ---");
        Vehicle v1 = new TwoWheeler("TN01AA1111");
        Vehicle v2 = new Car("TN02BB2222");
        Vehicle v3 = new ElectricCar("TN03CC3333");
        Vehicle v4 = new Car("TN04DD4444");

        int[] s1 = facility.allocate(v1);
        System.out.println("Allocated v1 (TwoWheeler): " + Arrays.toString(s1));
        int[] s2 = facility.allocate(v2);
        System.out.println("Allocated v2 (Car):        " + Arrays.toString(s2));
        int[] s3 = facility.allocate(v3);
        System.out.println("Allocated v3 (ElectricCar):" + Arrays.toString(s3));

        // 2. Boundary Cases: Last free slot & Facility Full
        System.out.println("\n--- 2. Boundary Cases (Capacity Limits) ---");
        int[] s4 = facility.allocate(v4);
        System.out.println("Allocated v4 (Last slot):  " + Arrays.toString(s4));

        Vehicle v5 = new TwoWheeler("TN05EE5555");
        int[] s5 = facility.allocate(v5);
        System.out.println("Allocated v5 (When full):  " + (s5 == null ? "null (Facility Full - Correct)" : Arrays.toString(s5)));

        // 3. Invalid Cases: Duplicate vehicle entry and invalid release
        System.out.println("\n--- 3. Invalid & Edge Cases ---");
        int[] dup = facility.allocate(new Car("TN02BB2222"));
        System.out.println("Duplicate entry result:    " + (dup == null ? "Rejected (Correct)" : "Allowed (Error)"));

        boolean relEmpty = facility.release(0, 1);
        facility.release(0, 1); // Release second time
        boolean relAgain = facility.release(0, 1);
        System.out.println("Release valid slot {0,1}:  " + relEmpty);
        System.out.println("Release same slot again:   " + relAgain + " (Correctly rejected)");

        boolean relInvalid = facility.release(5, 0);
        System.out.println("Release invalid slot {5,0}:" + relInvalid + " (Correctly rejected)");

        // 4. Financial Audit Reconciliation Scenario
        System.out.println("\n--- 4. Hand-Calculated Financial Reconciliation ---");
        // v1: TwoWheeler for 75 mins -> 2 hrs * 10 = Rs 20.0
        double fee1 = v1.calculateFee(75);
        // v2: Car for 60 mins -> 1 hr * 30 = Rs 30.0
        double fee2 = v2.calculateFee(60);
        // v3: ElectricCar for 130 mins -> 3 hrs * 30 = Rs 90 + (15 kWh * 12) = Rs 180 => Rs 270.0
        double fee3 = v3.calculateFee(130) + ((ElectricCar) v3).chargingFee(15);
        // v4: Pass holder car -> Rs 0.0
        double fee4 = 0.0;

        double totalCollection = fee1 + fee2 + fee3 + fee4;

        System.out.println("Vehicle 1 (Bike, 75 mins):   Rs " + fee1);
        System.out.println("Vehicle 2 (Car, 60 mins):    Rs " + fee2);
        System.out.println("Vehicle 3 (EV, 130m + 15kWh):Rs " + fee3);
        System.out.println("Vehicle 4 (Pass Holder Car): Rs " + fee4);
        System.out.println("----------------------------------------------");
        System.out.println("Total Actual System Collection: Rs " + totalCollection);
        System.out.println("Expected Hand-Calculated Value: Rs 320.0");
        System.out.println("Audit Reconciliation Result:    " + (totalCollection == 320.0 ? "MATCH (100% RECONCILED)" : "MISMATCH"));
    }
}
