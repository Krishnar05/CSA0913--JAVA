// Task 01: Domain Model Java Representation
public class DomainModel {

    // Candidate Model: Vehicle
    public static abstract class Vehicle {
        private final String regNo;
        public Vehicle(String regNo) { this.regNo = regNo; }
        public String getRegNo() { return regNo; }
        public abstract double getHourlyRate();
    }

    public static class TwoWheeler extends Vehicle {
        public TwoWheeler(String regNo) { super(regNo); }
        @Override public double getHourlyRate() { return 10.0; }
    }

    public static class Car extends Vehicle {
        public Car(String regNo) { super(regNo); }
        @Override public double getHourlyRate() { return 30.0; }
    }

    // Candidate Model: Parking Slot
    public static class ParkingSlot {
        private final int floor;
        private final int slotNumber;
        private Vehicle vehicle;

        public ParkingSlot(int floor, int slotNumber) {
            this.floor = floor;
            this.slotNumber = slotNumber;
        }

        public boolean isFree() { return vehicle == null; }
        public void park(Vehicle v) { this.vehicle = v; }
        public void vacate() { this.vehicle = null; }
        public Vehicle getVehicle() { return vehicle; }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 01: Candidate Domain Model Verification ===");
        Vehicle v1 = new TwoWheeler("TN01AA1111");
        Vehicle v2 = new Car("TN02BB2222");
        ParkingSlot slot = new ParkingSlot(0, 1);

        System.out.println("Slot initial free status: " + slot.isFree());
        slot.park(v1);
        System.out.println("Parked vehicle: " + slot.getVehicle().getRegNo() + " (Rate: Rs " + slot.getVehicle().getHourlyRate() + "/hr)");
        System.out.println("Slot free status after parking: " + slot.isFree());
        slot.vacate();
        System.out.println("Slot vacated successfully. Free status: " + slot.isFree());
    }
}
