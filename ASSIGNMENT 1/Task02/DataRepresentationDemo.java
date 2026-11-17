// Task 02: Data Representation and Precision Demonstration
public class DataRepresentationDemo {

    public static void main(String[] args) {
        System.out.println("=== Task 02: Data Representation & Precision Analysis ===");

        // 1. Jagged Array Construction (Demonstrating varying floor capacities)
        int[] floorCapacities = {2, 3, 1}; // Floor 0: 2 slots, Floor 1: 3 slots, Floor 2: 1 slot
        String[][] facility = new String[floorCapacities.length][];
        for (int i = 0; i < floorCapacities.length; i++) {
            facility[i] = new String[floorCapacities[i]];
        }
        System.out.println("Jagged Array Floors Initialized: " + facility.length + " floors.");
        for (int i = 0; i < facility.length; i++) {
            System.out.println("  Floor " + i + " capacity: " + facility[i].length + " slots.");
        }

        // 2. Demonstration of the Integer Division Bug
        int minutes = 65;
        double carRatePerHour = 30.0;

        // Truncated integer division (BUG)
        int flawedHours = minutes / 60;
        double flawedFee = flawedHours * carRatePerHour;

        // Correct integer arithmetic ceiling
        int correctHours = (minutes + 59) / 60;
        double correctFee = correctHours * carRatePerHour;

        // Floating point Math.ceil approach
        int ceilHours = (int) Math.ceil(minutes / 60.0);
        double ceilFee = ceilHours * carRatePerHour;

        System.out.println("\n--- Precision Loss & Casting Comparison (Duration: " + minutes + " mins) ---");
        System.out.println("Flawed Integer Division (minutes / 60): " + flawedHours + " hrs -> Rs " + flawedFee + " (WRONG - underbilled!)");
        System.out.println("Correct Integer Arithmetic ((minutes + 59) / 60): " + correctHours + " hrs -> Rs " + correctFee + " (CORRECT)");
        System.out.println("Math.ceil Float Division ((int) Math.ceil(minutes / 60.0)): " + ceilHours + " hrs -> Rs " + ceilFee + " (CORRECT)");
    }
}
