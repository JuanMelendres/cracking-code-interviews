import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CustomCollectorDemo {
    record Order(String customer, double amount) {}

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("alice", 100.0),
                new Order("bob", 50.0),
                new Order("alice", 75.0),
                new Order("carol", 200.0)
        );

        System.out.println("== Collectors.toMap() throws on duplicate keys without a merge function ==");
        try {
            Map<String, Double> broken = orders.stream()
                    .collect(Collectors.toMap(Order::customer, Order::amount));
            System.out.println("no exception (unexpected): " + broken);
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException: " + e.getMessage());
        }

        System.out.println();
        System.out.println("== Fixed: a merge function tells the collector how to combine duplicates ==");
        Map<String, Double> totals = orders.stream()
                .collect(Collectors.toMap(Order::customer, Order::amount, Double::sum));
        System.out.println("totals per customer: " + new java.util.TreeMap<>(totals));

        System.out.println();
        System.out.println("== groupingBy + downstream collector: count orders per customer ==");
        Map<String, Long> counts = orders.stream()
                .collect(Collectors.groupingBy(Order::customer, Collectors.counting()));
        System.out.println("order counts: " + new java.util.TreeMap<>(counts));

        System.out.println();
        System.out.println("== A real custom Collector: running total as a formatted string ==");
        Collector<Order, double[], String> runningTotalCollector = Collector.of(
                () -> new double[1],
                (acc, order) -> acc[0] += order.amount(),
                (acc1, acc2) -> { acc1[0] += acc2[0]; return acc1; },
                acc -> String.format("$%.2f", acc[0])
        );
        String grandTotal = orders.stream().collect(runningTotalCollector);
        System.out.println("grand total via custom collector: " + grandTotal);
        double expected = orders.stream().mapToDouble(Order::amount).sum();
        System.out.println("cross-checked against mapToDouble().sum(): $" + String.format("%.2f", expected)
                + (grandTotal.equals(String.format("$%.2f", expected)) ? "  -- MATCH" : "  -- MISMATCH"));
    }
}
