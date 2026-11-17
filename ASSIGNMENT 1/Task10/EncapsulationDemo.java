// Task 10: Encapsulation and Data Validation Demonstration
public class EncapsulationDemo {

    public static class ValidatedTicket {
        private final long ticketId;
        private final String regNo;
        private int durationMinutes;

        public ValidatedTicket(long ticketId, String regNo) {
            if (regNo == null || regNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number cannot be null or empty.");
            }
            this.ticketId = ticketId;
            this.regNo = regNo.trim().toUpperCase();
        }

        public long getTicketId() { return ticketId; }
        public String getRegNo() { return regNo; }
        public int getDurationMinutes() { return durationMinutes; }

        public void setDurationMinutes(int durationMinutes) {
            if (durationMinutes < 0) {
                throw new IllegalArgumentException("Validation Error: Duration cannot be negative (" + durationMinutes + ").");
            }
            this.durationMinutes = durationMinutes;
        }

        @Override
        public String toString() {
            return "Ticket #" + ticketId + " [" + regNo + "] Duration: " + durationMinutes + " mins";
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 10: Encapsulation & Validation Demo ===");

        // 1. Valid instantiation
        ValidatedTicket ticket = new ValidatedTicket(2001L, "tn01ab1234");
        System.out.println("Created: " + ticket);

        // 2. Setting valid duration
        ticket.setDurationMinutes(90);
        System.out.println("Updated: " + ticket);

        // 3. Defensive rejection of negative duration
        try {
            System.out.println("Attempting to set negative duration (-30 mins)...");
            ticket.setDurationMinutes(-30);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught Expected Security Exception: " + e.getMessage());
        }

        // 4. Autoboxing == vs equals() demonstration
        Integer idA = 500;
        Integer idB = 500;
        System.out.println("\n--- Wrapper Autoboxing Comparison (id=500) ---");
        System.out.println("idA == idB: " + (idA == idB) + " (Reference check fails outside cache -128..127)");
        System.out.println("idA.equals(idB): " + idA.equals(idB) + " (Value check succeeds)");
    }
}
