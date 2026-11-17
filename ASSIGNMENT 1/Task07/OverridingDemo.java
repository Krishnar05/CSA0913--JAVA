// Task 07: Method Overriding, equals(), hashCode(), and final Keyword Demonstration
public class OverridingDemo {

    public static abstract class Vehicle {
        private final String regNo; // final variable: immutable license plate

        protected Vehicle(String regNo) {
            this.regNo = regNo;
        }

        public String getRegNo() { return regNo; }

        protected abstract double ratePerHour();

        // final template method: core billing ceiling logic cannot be tampered with by subclasses
        public final double calculateFee(int minutes) {
            int hours = (minutes + 59) / 60;
            return hours * ratePerHour();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Vehicle)) return false;
            Vehicle vehicle = (Vehicle) o;
            return regNo != null && regNo.equals(vehicle.regNo);
        }

        @Override
        public int hashCode() {
            return regNo != null ? regNo.hashCode() : 0;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "{" + "regNo='" + regNo + '\'' + '}';
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

    public static void main(String[] args) {
        System.out.println("=== Task 07: Method Overriding & Object Contract Demo ===");

        Vehicle v1 = new Car("TN09AZ9999");
        Vehicle v2 = new Car("TN09AZ9999");
        Vehicle v3 = new TwoWheeler("TN09AZ9999");

        System.out.println("v1: " + v1);
        System.out.println("v2: " + v2);
        System.out.println("Reference equality (v1 == v2): " + (v1 == v2));
        System.out.println("Semantic equality (v1.equals(v2)): " + v1.equals(v2) + " (Both represent same plate)");
        System.out.println("HashCodes equal? " + (v1.hashCode() == v2.hashCode()));

        System.out.println("\nFee for 75 mins parking:");
        System.out.println("Car fee: Rs " + v1.calculateFee(75));
        System.out.println("Bike fee: Rs " + v3.calculateFee(75));
    }
}
