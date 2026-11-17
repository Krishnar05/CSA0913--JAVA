// Task 06: Inheritance Hierarchy and Constructor Tracing Demo
public class InheritanceDemo {

    public static abstract class Vehicle {
        private final String regNo;

        public Vehicle(String regNo) {
            System.out.println("  [1] Vehicle constructor executing for: " + regNo);
            this.regNo = regNo;
        }

        public String getRegNo() { return regNo; }
        public abstract double ratePerHour();
    }

    public static class Car extends Vehicle {
        public Car(String regNo) {
            super(regNo);
            System.out.println("  [2] Car constructor executing for: " + regNo);
        }

        @Override
        public double ratePerHour() { return 30.0; }
    }

    public static class ElectricCar extends Car {
        public ElectricCar(String regNo) {
            super(regNo);
            System.out.println("  [3] ElectricCar constructor executing for: " + regNo);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Task 06: Inheritance & Constructor Tracing Demo ===");
        System.out.println("Creating ElectricCar instance 'TN03EV9999'...");
        ElectricCar ec = new ElectricCar("TN03EV9999");
        System.out.println("Object successfully created. Plate: " + ec.getRegNo() + ", Rate: Rs " + ec.ratePerHour() + "/hr");
    }
}
