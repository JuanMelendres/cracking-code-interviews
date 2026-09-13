import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Real, compiling worked example of a classic OOD interview prompt: "design a
// parking lot." Demonstrates the standard OOD methodology this chapter teaches
// -- identify entities, assign single responsibilities, model relationships via
// interfaces so the design stays open to new vehicle/spot types (OCP) -- then
// exercises it with real park/remove/fee-calculation behavior end to end.
public class ParkingLotDemo {

    enum VehicleSize { MOTORCYCLE, CAR, BUS }

    interface Vehicle {
        String licensePlate();
        VehicleSize size();
    }

    record Motorcycle(String licensePlate) implements Vehicle {
        public VehicleSize size() { return VehicleSize.MOTORCYCLE; }
    }
    record Car(String licensePlate) implements Vehicle {
        public VehicleSize size() { return VehicleSize.CAR; }
    }
    record Bus(String licensePlate) implements Vehicle {
        public VehicleSize size() { return VehicleSize.BUS; }
    }

    enum SpotSize { SMALL, MEDIUM, LARGE }

    static class ParkingSpot {
        final String id;
        final SpotSize spotSize;
        Vehicle occupant;

        ParkingSpot(String id, SpotSize spotSize) { this.id = id; this.spotSize = spotSize; }

        boolean canFit(Vehicle v) {
            return switch (v.size()) {
                case MOTORCYCLE -> true; // fits any spot
                case CAR -> spotSize == SpotSize.MEDIUM || spotSize == SpotSize.LARGE;
                case BUS -> spotSize == SpotSize.LARGE;
            };
        }

        boolean isFree() { return occupant == null; }
    }

    static class Ticket {
        final Vehicle vehicle;
        final ParkingSpot spot;
        final LocalDateTime entryTime;

        Ticket(Vehicle vehicle, ParkingSpot spot, LocalDateTime entryTime) {
            this.vehicle = vehicle; this.spot = spot; this.entryTime = entryTime;
        }
    }

    interface FeeCalculator {
        double calculate(Vehicle vehicle, Duration parkedDuration);
    }

    // Real, simple tiered rate -- the point of injecting this as an interface
    // (DIP) is that pricing policy can change without touching ParkingLot at all.
    static class HourlyFeeCalculator implements FeeCalculator {
        public double calculate(Vehicle vehicle, Duration parkedDuration) {
            double hours = Math.ceil(parkedDuration.toMinutes() / 60.0);
            double ratePerHour = switch (vehicle.size()) {
                case MOTORCYCLE -> 1.0;
                case CAR -> 2.5;
                case BUS -> 5.0;
            };
            return Math.max(1, hours) * ratePerHour;
        }
    }

    static class ParkingLot {
        private final List<ParkingSpot> spots;
        private final FeeCalculator feeCalculator;

        ParkingLot(List<ParkingSpot> spots, FeeCalculator feeCalculator) {
            this.spots = spots;
            this.feeCalculator = feeCalculator;
        }

        Optional<Ticket> park(Vehicle vehicle, LocalDateTime now) {
            for (ParkingSpot spot : spots) {
                if (spot.isFree() && spot.canFit(vehicle)) {
                    spot.occupant = vehicle;
                    return Optional.of(new Ticket(vehicle, spot, now));
                }
            }
            return Optional.empty(); // lot full for this vehicle size
        }

        double removeAndCharge(Ticket ticket, LocalDateTime exitTime) {
            ticket.spot.occupant = null;
            return feeCalculator.calculate(ticket.vehicle, Duration.between(ticket.entryTime, exitTime));
        }

        long availableSpots(SpotSize size) {
            return spots.stream().filter(s -> s.isFree() && s.spotSize == size).count();
        }
    }

    public static void main(String[] args) {
        List<ParkingSpot> spots = new ArrayList<>();
        spots.add(new ParkingSpot("S1", SpotSize.SMALL));
        spots.add(new ParkingSpot("M1", SpotSize.MEDIUM));
        spots.add(new ParkingSpot("M2", SpotSize.MEDIUM));
        spots.add(new ParkingSpot("L1", SpotSize.LARGE));
        ParkingLot lot = new ParkingLot(spots, new HourlyFeeCalculator());

        LocalDateTime t0 = LocalDateTime.of(2026, 1, 1, 8, 0);
        System.out.println("=== Park a motorcycle, a car, and a bus ===");
        Ticket moto = lot.park(new Motorcycle("MOTO-1"), t0).orElseThrow();
        System.out.println("  Motorcycle parked at spot " + moto.spot.id + " (a SMALL spot -- motorcycles fit anywhere)");
        Ticket car = lot.park(new Car("CAR-1"), t0).orElseThrow();
        System.out.println("  Car parked at spot " + car.spot.id + " (a MEDIUM spot -- cars can't use SMALL)");
        Ticket bus = lot.park(new Bus("BUS-1"), t0).orElseThrow();
        System.out.println("  Bus parked at spot " + bus.spot.id + " (a LARGE spot -- only LARGE fits a bus)");

        System.out.println();
        System.out.println("=== Try to park a second car -- only M2 is left, and it fits ===");
        Ticket car2 = lot.park(new Car("CAR-2"), t0).orElseThrow();
        System.out.println("  Car #2 parked at spot " + car2.spot.id);

        System.out.println();
        System.out.println("=== Try to park a THIRD car -- lot has no MEDIUM or LARGE spots left ===");
        Optional<Ticket> car3 = lot.park(new Car("CAR-3"), t0);
        System.out.println("  Result: " + (car3.isPresent() ? "parked (BUG)" : "REJECTED -- lot full for this vehicle size"));
        System.out.println("  Available MEDIUM spots right now: " + lot.availableSpots(SpotSize.MEDIUM));

        System.out.println();
        System.out.println("=== Car #1 leaves after 2h15m -- real fee calculated from real elapsed time ===");
        LocalDateTime exit = t0.plusHours(2).plusMinutes(15);
        double fee = lot.removeAndCharge(car, exit);
        System.out.printf("  Fee for CAR-1 (2h15m, rounds up to 3h @ $2.50/h) = $%.2f%n", fee);

        System.out.println();
        System.out.println("=== Now that Car #1's spot is free, the previously-rejected Car #3 can park ===");
        Optional<Ticket> car3Retry = lot.park(new Car("CAR-3"), exit);
        System.out.println("  Result: " + (car3Retry.isPresent()
                ? "parked at spot " + car3Retry.get().spot.id
                : "still rejected (BUG)"));
    }
}
