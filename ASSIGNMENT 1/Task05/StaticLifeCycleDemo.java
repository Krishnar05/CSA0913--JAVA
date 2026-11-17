// Task 05: Static Members and Object Life Cycle Demonstration
public class StaticLifeCycleDemo {

    public static class Ticket {
        private static long sequenceCounter = 1000L; // Static variable shared across all instances
        private final long id;
        private final String regNo;

        static {
            System.out.println("[Static Init]: Ticket class loaded. Sequence counter initialized.");
        }

        public Ticket(String regNo) {
            this.id = ++sequenceCounter;
            this.regNo = regNo;
        }

        public static long getTotalIssued() {
            return sequenceCounter - 1000L;
        }

        @Override
        public String toString() {
            return "Ticket #" + id + " for " + regNo;
        }
    }

    public static void processCheckout(Ticket t) {
        System.out.println("Processing checkout for: " + t);
        // Once this method exits, local reference 't' popped from stack
    }

    public static void main(String[] args) {
        System.out.println("=== Task 05: Static Members & Object Life Cycle ===");

        Ticket t1 = new Ticket("TN01AA1111");
        Ticket t2 = new Ticket("TN02BB2222");
        Ticket t3 = new Ticket("TN03CC3333");

        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t3);
        System.out.println("Total Tickets Issued via static method: " + Ticket.getTotalIssued());

        // Object Life Cycle and GC Demonstration
        processCheckout(t1);
        t1 = null; // Reference nulled; the object on the heap is now eligible for GC
        System.out.println("t1 reference set to null. Object now eligible for Garbage Collection.");
    }
}
