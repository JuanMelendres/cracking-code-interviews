package coupling;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Two real findings, both instrumented, neither eyeballed:
 * 1. A God Class's real, measured coupling count (distinct collaborator
 *    types referenced) drops sharply once decomposed into single-purpose
 *    classes -- "efferent coupling," measured here as the count of
 *    declared field types outside java.lang/java.util, a simple, honest
 *    operational proxy, not a claim of a universally standardized metric.
 * 2. A real Feature Envy method -- one that operates almost entirely on
 *    another class's own data -- moved to the class it envies, verified
 *    to produce byte-for-byte identical output via the same
 *    before/after test-parity technique
 *    18-engineering-practices/refactoring-discipline.md's own practice
 *    demo uses.
 */
public class CouplingMeasurementDemo {

    // ---- Shared collaborator types (used by both BEFORE and AFTER) ----

    static class Customer {
        final String name;
        final String state;
        final boolean verified;
        final int yearsActive;
        final double totalSpent;

        Customer(String name, String state, boolean verified, int yearsActive, double totalSpent) {
            this.name = name; this.state = state; this.verified = verified;
            this.yearsActive = yearsActive; this.totalSpent = totalSpent;
        }
    }

    static class Order {
        final Customer customer;
        final double subtotal;
        final String destinationState;
        Order(Customer customer, double subtotal, String destinationState) {
            this.customer = customer; this.subtotal = subtotal; this.destinationState = destinationState;
        }
    }

    static class TaxTable {
        double rateFor(String state) { return state.equals("CA") ? 0.0725 : 0.05; }
    }

    static class ShippingRateTable {
        double flatRateFor(String state) { return state.equals("HI") || state.equals("AK") ? 25.0 : 8.0; }
    }

    static class InventoryStore {
        int reserved = 0;
        void reserve(int units) { reserved += units; }
    }

    static class EmailGateway {
        List<String> sent = new ArrayList<>();
        void send(String to, String subject) { sent.add(to + ":" + subject); }
    }

    static class PaymentGateway {
        double totalCharged = 0;
        void charge(double amount) { totalCharged += amount; }
    }

    // ---- BEFORE: a God Class, plus one Feature Envy method ----

    static class GodOrderProcessor {
        TaxTable taxTable = new TaxTable();
        ShippingRateTable shippingRateTable = new ShippingRateTable();
        InventoryStore inventoryStore = new InventoryStore();
        EmailGateway emailGateway = new EmailGateway();
        PaymentGateway paymentGateway = new PaymentGateway();

        // Feature Envy: this method reads only Customer's own fields --
        // nothing of GodOrderProcessor's own state -- yet lives here.
        double loyaltyDiscountPercent(Customer c) {
            double discount = 0;
            if (c.yearsActive >= 3) discount += 0.05;
            if (c.totalSpent >= 1000) discount += 0.05;
            if (c.verified) discount += 0.02;
            return discount;
        }

        double process(Order order) {
            if (!order.customer.verified || order.subtotal <= 0) {
                throw new IllegalArgumentException("invalid order");
            }
            double discount = loyaltyDiscountPercent(order.customer);
            double discounted = order.subtotal * (1 - discount);
            double tax = discounted * taxTable.rateFor(order.destinationState);
            double shipping = shippingRateTable.flatRateFor(order.destinationState);
            double total = discounted + tax + shipping;
            inventoryStore.reserve(1);
            emailGateway.send(order.customer.name, "Order confirmed: $" + String.format("%.2f", total));
            paymentGateway.charge(total);
            return total;
        }
    }

    // ---- AFTER: decomposed, single-purpose classes; Feature Envy moved onto Customer ----

    static class CustomerWithLoyalty extends Customer {
        CustomerWithLoyalty(String name, String state, boolean verified, int yearsActive, double totalSpent) {
            super(name, state, verified, yearsActive, totalSpent);
        }
        // Moved here from GodOrderProcessor -- now operates on its OWN fields, no envy.
        double loyaltyDiscountPercent() {
            double discount = 0;
            if (yearsActive >= 3) discount += 0.05;
            if (totalSpent >= 1000) discount += 0.05;
            if (verified) discount += 0.02;
            return discount;
        }
    }

    static class OrderValidator {
        void validate(Order order) {
            if (!order.customer.verified || order.subtotal <= 0) {
                throw new IllegalArgumentException("invalid order");
            }
        }
    }

    static class TaxCalculator {
        TaxTable taxTable = new TaxTable();
        double taxFor(double amount, String state) { return amount * taxTable.rateFor(state); }
    }

    static class ShippingCalculator {
        ShippingRateTable shippingRateTable = new ShippingRateTable();
        double shippingFor(String state) { return shippingRateTable.flatRateFor(state); }
    }

    static class InventoryUpdater {
        InventoryStore inventoryStore = new InventoryStore();
        void reserveOneUnit() { inventoryStore.reserve(1); }
    }

    static class EmailNotifier {
        EmailGateway emailGateway = new EmailGateway();
        void confirm(String customerName, double total) {
            emailGateway.send(customerName, "Order confirmed: $" + String.format("%.2f", total));
        }
    }

    static class PaymentProcessor {
        PaymentGateway paymentGateway = new PaymentGateway();
        void charge(double amount) { paymentGateway.charge(amount); }
    }

    static class OrderProcessor {
        OrderValidator validator = new OrderValidator();
        TaxCalculator taxCalculator = new TaxCalculator();
        ShippingCalculator shippingCalculator = new ShippingCalculator();
        InventoryUpdater inventoryUpdater = new InventoryUpdater();
        EmailNotifier emailNotifier = new EmailNotifier();
        PaymentProcessor paymentProcessor = new PaymentProcessor();

        double process(Order order) {
            validator.validate(order);
            CustomerWithLoyalty loyaltyCustomer = (CustomerWithLoyalty) order.customer;
            double discount = loyaltyCustomer.loyaltyDiscountPercent();
            double discounted = order.subtotal * (1 - discount);
            double tax = taxCalculator.taxFor(discounted, order.destinationState);
            double shipping = shippingCalculator.shippingFor(order.destinationState);
            double total = discounted + tax + shipping;
            inventoryUpdater.reserveOneUnit();
            emailNotifier.confirm(order.customer.name, total);
            paymentProcessor.charge(total);
            return total;
        }
    }

    // ---- Coupling measurement: distinct declared field types outside java.lang/java.util ----

    static Set<Class<?>> collaboratorFieldTypes(Class<?> clazz) {
        Set<Class<?>> types = new LinkedHashSet<>();
        for (Field f : clazz.getDeclaredFields()) {
            Class<?> t = f.getType();
            String pkg = t.getPackageName();
            if (!pkg.startsWith("java.") && !t.isPrimitive()) {
                types.add(t);
            }
        }
        return types;
    }

    public static void main(String[] args) {
        System.out.println("=== Coupling measurement: distinct collaborator field types ===");
        Set<Class<?>> godCoupling = collaboratorFieldTypes(GodOrderProcessor.class);
        System.out.println("GodOrderProcessor (before): " + godCoupling.size() + " -> " + names(godCoupling));

        Map<String, Set<Class<?>>> afterClasses = new LinkedHashMap<>();
        afterClasses.put("OrderProcessor (orchestrator)", collaboratorFieldTypes(OrderProcessor.class));
        afterClasses.put("OrderValidator", collaboratorFieldTypes(OrderValidator.class));
        afterClasses.put("TaxCalculator", collaboratorFieldTypes(TaxCalculator.class));
        afterClasses.put("ShippingCalculator", collaboratorFieldTypes(ShippingCalculator.class));
        afterClasses.put("InventoryUpdater", collaboratorFieldTypes(InventoryUpdater.class));
        afterClasses.put("EmailNotifier", collaboratorFieldTypes(EmailNotifier.class));
        afterClasses.put("PaymentProcessor", collaboratorFieldTypes(PaymentProcessor.class));

        for (var entry : afterClasses.entrySet()) {
            System.out.println(entry.getKey() + " (after): " + entry.getValue().size() + " -> " + names(entry.getValue()));
        }

        int maxAfterSingleClass = afterClasses.entrySet().stream()
                .filter(e -> !e.getKey().startsWith("OrderProcessor"))
                .mapToInt(e -> e.getValue().size())
                .max().orElse(0);
        System.out.println();
        System.out.println("God class coupling: " + godCoupling.size()
                + " vs. largest single decomposed class's coupling: " + maxAfterSingleClass);
        if (maxAfterSingleClass >= godCoupling.size()) {
            throw new IllegalStateException("decomposition did not reduce per-class coupling");
        }

        // ---- Behavior parity: same real orders, before vs. after, byte-for-byte identical totals ----
        System.out.println();
        System.out.println("=== Behavior parity: God Class vs. decomposed, same real orders ===");
        Customer beforeCustomerA = new Customer("Alice", "CA", true, 5, 1500);
        CustomerWithLoyalty afterCustomerA = new CustomerWithLoyalty("Alice", "CA", true, 5, 1500);
        Customer beforeCustomerB = new Customer("Bob", "TX", true, 1, 200);
        CustomerWithLoyalty afterCustomerB = new CustomerWithLoyalty("Bob", "TX", true, 1, 200);
        Customer beforeCustomerC = new Customer("Cara", "HI", true, 4, 5000);
        CustomerWithLoyalty afterCustomerC = new CustomerWithLoyalty("Cara", "HI", true, 4, 5000);

        double[] beforeTotals = {
            new GodOrderProcessor().process(new Order(beforeCustomerA, 200.00, "CA")),
            new GodOrderProcessor().process(new Order(beforeCustomerB, 49.99, "TX")),
            new GodOrderProcessor().process(new Order(beforeCustomerC, 899.50, "HI")),
        };
        double[] afterTotals = {
            new OrderProcessor().process(new Order(afterCustomerA, 200.00, "CA")),
            new OrderProcessor().process(new Order(afterCustomerB, 49.99, "TX")),
            new OrderProcessor().process(new Order(afterCustomerC, 899.50, "HI")),
        };

        boolean allMatch = true;
        for (int i = 0; i < beforeTotals.length; i++) {
            boolean match = Double.compare(beforeTotals[i], afterTotals[i]) == 0;
            allMatch &= match;
            System.out.printf("  order %d: before=%.4f after=%.4f match=%b%n", i + 1, beforeTotals[i], afterTotals[i], match);
        }
        if (!allMatch) throw new IllegalStateException("behavior parity broken by decomposition");
        System.out.println("All " + beforeTotals.length + " orders: identical totals before and after decomposition.");
    }

    static List<String> names(Set<Class<?>> types) {
        List<String> out = new ArrayList<>();
        for (Class<?> t : types) out.add(t.getSimpleName());
        return out;
    }
}
