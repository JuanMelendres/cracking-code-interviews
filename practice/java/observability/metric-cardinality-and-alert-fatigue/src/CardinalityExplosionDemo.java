import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.UUID;

public class CardinalityExplosionDemo {
    static long usedMemoryBytes() {
        Runtime rt = Runtime.getRuntime();
        for (int i = 0; i < 3; i++) { System.gc(); try { Thread.sleep(50); } catch (InterruptedException ignored) {} }
        return rt.totalMemory() - rt.freeMemory();
    }

    public static void main(String[] args) {
        int requests = 20_000;

        long before1 = usedMemoryBytes();
        // LOW cardinality: label is a bounded route template + status code
        MeterRegistry lowCardRegistry = new SimpleMeterRegistry();
        String[] routes = {"/api/orders", "/api/users", "/api/payments"};
        String[] statuses = {"200", "404", "500"};
        for (int i = 0; i < requests; i++) {
            String route = routes[i % routes.length];
            String status = statuses[i % statuses.length];
            Counter.builder("http.server.requests")
                    .tag("route", route)
                    .tag("status", status)
                    .register(lowCardRegistry)
                    .increment();
        }
        long after1 = usedMemoryBytes();

        long before2 = usedMemoryBytes();
        // HIGH cardinality: label includes a per-request unique ID (a real,
        // common production mistake -- e.g. tagging by user ID or request ID)
        MeterRegistry highCardRegistry = new SimpleMeterRegistry();
        for (int i = 0; i < requests; i++) {
            String requestId = UUID.randomUUID().toString();
            Counter.builder("http.server.requests")
                    .tag("route", routes[i % routes.length])
                    .tag("request_id", requestId)
                    .register(highCardRegistry)
                    .increment();
        }
        long after2 = usedMemoryBytes();

        System.out.println("Requests processed (each registry): " + requests);
        System.out.println();
        System.out.println("LOW-cardinality  (route + status code):  distinct time series (Meters) = "
                + lowCardRegistry.getMeters().size()
                + ", heap delta = " + (after1 - before1) + " bytes");
        System.out.println("HIGH-cardinality (route + raw request_id): distinct time series (Meters) = "
                + highCardRegistry.getMeters().size()
                + ", heap delta = " + (after2 - before2) + " bytes");
        System.out.println();
        double meterRatio = (double) highCardRegistry.getMeters().size() / lowCardRegistry.getMeters().size();
        double memRatio = (double) (after2 - before2) / Math.max(1, (after1 - before1));
        System.out.printf("High-cardinality registry created %.0fx more distinct time series "
                + "and used ~%.0fx more heap for the identical request volume.%n", meterRatio, memRatio);
    }
}
