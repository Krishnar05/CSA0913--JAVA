// Task 04: Class Design, Constructors, and Chaining Demonstration
public class ConstructorDemo {

    public static class ParkingTicket {
        private final long ticketId;
        private final String regNo;
        private final int floor;
        private final int slot;
        private final int entryMinutes;
        private final boolean isPassHolder;

        // Canonical Parameterized Constructor
        public ParkingTicket(long ticketId, String regNo, int floor, int slot, int entryMinutes, boolean isPassHolder) {
            this.ticketId = ticketId;
            this.regNo = regNo;
            this.floor = floor;
            this.slot = slot;
            this.entryMinutes = entryMinutes;
            this.isPassHolder = isPassHolder;
        }

        // Overloaded Constructor 1: Defaults entryMinutes = 0, isPassHolder = false
        public ParkingTicket(long ticketId, String regNo, int floor, int slot) {
            this(ticketId, regNo, floor, slot, 0, false); // Constructor Chaining
        }

        // Overloaded Constructor 2: Quick visitor ticket
        public ParkingTicket(long ticketId, String regNo) {
            this(ticketId, regNo, -1, -1, 0, false); // Constructor Chaining
        }

        // Copy Constructor: For duplicate ticket issuance
        public ParkingTicket(ParkingTicket original) {
            this(original.ticketId, original.regNo, original.floor, original.slot, original.entryMinutes, original.isPassHolder);
        }

        @Override
        public String toString() {
            return "Ticket #" + ticketId + " [RegNo=" + regNo + ", Floor=" + floor + ", Slot=" + slot
                    + ", Entry=" + entryMinutes + "m, Pass=" + isPassHolder + "]";
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 04: Constructor and Object Creation Demo ===");

        // 1. Parameterized instantiation
        ParkingTicket t1 = new ParkingTicket(1001L, "TN01AB1111", 0, 1, 45, true);
        System.out.println("t1 (Full Parameterized): " + t1);

        // 2. Chained overloaded constructor
        ParkingTicket t2 = new ParkingTicket(1002L, "TN02CD2222", 1, 0);
        System.out.println("t2 (Chained Overload):   " + t2);

        // 3. Copy constructor (duplicate ticket)
        ParkingTicket t1Duplicate = new ParkingTicket(t1);
        System.out.println("t1Duplicate (Copy Ctor): " + t1Duplicate);
        System.out.println("Are references identical? " + (t1 == t1Duplicate) + " (Independent heap instances)");
    }
}
