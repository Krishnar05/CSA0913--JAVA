// Task 12: Day-4 Design Challenge — Adapted Parking System with E-Bikes, Delivery Vans, Passes, and 5-Floor Jagged Array
import java.util.Arrays;

public class AdaptedParkingSystem {

    public interface Pass {
        boolean isValid();
        double getPassCost();
    }

    public static class OneDayPass implements Pass {
        private final long issueTimeEpoch;
        private static final double ONE_DAY_FLAT_FEE = 150.0;

        public OneDayPass() {
            this.issueTimeEpoch = System.currentTimeMillis();
        }

        @Override public boolean isValid() { return true; }
        @Override public double getPassCost() { return ONE_DAY_FLAT_FEE; }
    }

    public static abstract class Vehicle {
        private final String regNo;

        protected Vehicle(String regNo) {
            this.regNo = regNo.trim().toUpperCase();
        }

        public String getRegNo() { return regNo; }
        protected abstract double ratePerHour();

        // Backward-compatible standard fee
        public double calculateFee(int durationMinutes) {
            return calculateFee(12, durationMinutes); // Default to midday
        }

        // Time-aware fee calculation (entryHour in 24-hr format: 0..23)
        public double calculateFee(int entryHour, int durationMinutes) {
            int hours = (durationMinutes + 59) / 60;
            if (hours == 0) hours = 1;
            return hours * ratePerHour();
        }
    }

    public static class EBike extends Vehicle {
        private static final double FLAT_FEE = 15.0;

        public EBike(String regNo) { super(regNo); }

        @Override protected double ratePerHour() { return FLAT_FEE; }

        @Override
        public double calculateFee(int entryHour, int durationMinutes) {
            return FLAT_FEE; // Flat fee regardless of duration or entry time
        }
    }

    public static class DeliveryVan extends Vehicle {
        private static final double BASE_RATE = 30.0; // Standard car rate

        public DeliveryVan(String regNo) { super(regNo); }

        @Override protected double ratePerHour() { return BASE_RATE; }

        @Override
        public double calculateFee(int entryHour, int durationMinutes) {
            int hours = (durationMinutes + 59) / 60;
            if (hours == 0) hours = 1;

            // Peak hours: 9 AM to 5 PM (9:00 to 17:00) -> Double car rate (Rs 60.0/hr)
            boolean isPeak = (entryHour >= 9 && entryHour < 17);
            double effectiveRate = isPeak ? (BASE_RATE * 2.0) : BASE_RATE;

            return hours * effectiveRate;
        }
    }

    public static class ExpandedParkingFacility {
        private final Vehicle[][] slots;

        public ExpandedParkingFacility(int[] slotsPerFloor) {
            slots = new Vehicle[slotsPerFloor.length][];
            for (int f = 0; f < slotsPerFloor.length; f++) {
                slots[f] = new Vehicle[slotsPerFloor[f]];
            }
        }

        public int getTotalCapacity() {
            int total = 0;
            for (Vehicle[] floor : slots) total += floor.length;
            return total;
        }

        public int[] allocate(Vehicle v) {
            for (int f = 0; f < slots.length; f++) {
                for (int s = 0; s < slots[f].length; s++) {
                    if (slots[f][s] == null) {
                        slots[f][s] = v;
                        return new int[]{f, s};
                    }
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("   TASK 12: DAY-4 DESIGN CHALLENGE — ADAPTED PARKING SYSTEM       ");
        System.out.println("==================================================================");

        // 1. 5-Floor Extended Facility with Varying Capacities
        int[] floorConfig = {20, 30, 40, 50, 60};
        ExpandedParkingFacility facility = new ExpandedParkingFacility(floorConfig);
        System.out.println("Extended Facility Initialized:");
        System.out.println("  Total Floors:   " + floorConfig.length);
        System.out.println("  Floor Layout:   " + Arrays.toString(floorConfig));
        System.out.println("  Total Capacity: " + facility.getTotalCapacity() + " slots across 5 floors.");

        // 2. Testing E-Bike Flat Rate
        EBike ebike = new EBike("TN05EB0001");
        double ebikeFee1 = ebike.calculateFee(10, 45);   // 45 mins
        double ebikeFee2 = ebike.calculateFee(10, 360);  // 6 hours
        System.out.println("\n--- 2. E-Bike Flat Rate Test ---");
        System.out.println("EBike (45 mins stay):  Rs " + ebikeFee1 + " (Flat Rate - Correct)");
        System.out.println("EBike (360 mins stay): Rs " + ebikeFee2 + " (Flat Rate - Correct)");

        // 3. Testing Delivery Van Surge Pricing
        DeliveryVan van = new DeliveryVan("TN06DV7777");
        // Peak Stay: Entered at 10 AM (10:00), parked for 120 mins (2 hrs) -> 2 * Rs 60 = Rs 120.0
        double vanPeakFee = van.calculateFee(10, 120);
        // Off-Peak Stay: Entered at 8 PM (20:00), parked for 120 mins (2 hrs) -> 2 * Rs 30 = Rs 60.0
        double vanOffPeakFee = van.calculateFee(20, 120);
        System.out.println("\n--- 3. Delivery Van Dynamic Pricing Test ---");
        System.out.println("Van Peak (Entry 10 AM, 2 hrs @ Rs 60/hr): Rs " + vanPeakFee);
        System.out.println("Van Off-Peak (Entry 8 PM, 2 hrs @ Rs 30/hr): Rs " + vanOffPeakFee);

        // 4. One-Day Pass Demonstration
        OneDayPass visitorPass = new OneDayPass();
        System.out.println("\n--- 4. Visitor One-Day Pass Test ---");
        System.out.println("One-Day Pass Issued. Cost: Rs " + visitorPass.getPassCost());
        System.out.println("Pass Validity Status:      " + visitorPass.isValid());

        // 5. Allocation in Extended Facility
        int[] ebikeSlot = facility.allocate(ebike);
        int[] vanSlot = facility.allocate(van);
        System.out.println("\n--- 5. Allocation in 5-Floor Facility ---");
        System.out.println("EBike allocated slot: " + Arrays.toString(ebikeSlot));
        System.out.println("Van allocated slot:   " + Arrays.toString(vanSlot));
    }
}
