// Task 09: Abstraction with Abstract Classes, Interfaces, and Default Methods
public class AbstractionDemo {

    public interface Chargeable {
        double chargingFee(int kWh);

        // Default method: provides default telemetry message
        default String getChargingSummary(int kWh) {
            return "Dispensed " + kWh + " kWh energy via smart grid terminal.";
        }

        // Static helper method in interface
        static double getGovernmentTaxRate() {
            return 0.05; // 5% green cess
        }
    }

    public static abstract class Vehicle {
        private final String regNo;

        protected Vehicle(String regNo) {
            this.regNo = regNo;
        }

        public String getRegNo() { return regNo; }

        protected abstract double ratePerHour();

        public double calculateFee(int minutes) {
            int hours = (minutes + 59) / 60;
            return hours * ratePerHour();
        }
    }

    public static class ElectricCar extends Vehicle implements Chargeable {
        public ElectricCar(String regNo) { super(regNo); }

        @Override
        protected double ratePerHour() { return 30.0; }

        @Override
        public double chargingFee(int kWh) {
            double baseFee = kWh * 12.0;
            return baseFee + (baseFee * Chargeable.getGovernmentTaxRate());
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 09: Abstraction & Interface Demonstration ===");

        ElectricCar ev = new ElectricCar("TN07EV8888");
        int durationMinutes = 90;
        int energyKWh = 15;

        double parkingFee = ev.calculateFee(durationMinutes);
        double chargingFee = ev.chargingFee(energyKWh);
        double total = parkingFee + chargingFee;

        System.out.println("EV Vehicle: " + ev.getRegNo());
        System.out.println("Parking Fee (90 mins -> 2 hrs @ Rs 30/hr): Rs " + parkingFee);
        System.out.println("Charging Fee (15 kWh @ Rs 12/kWh + 5% cess): Rs " + chargingFee);
        System.out.println("Total Amount Due: Rs " + total);
        System.out.println("Interface Default Method: " + ev.getChargingSummary(energyKWh));
    }
}
