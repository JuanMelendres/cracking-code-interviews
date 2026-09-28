import java.util.Map;

/**
 * Factory Method and Abstract Factory, executed against a realistic backend
 * problem: selecting a payment provider, and selecting a whole family of
 * region-specific components at once.
 *
 * Java 21. Pure JDK, no dependencies.
 *
 * Compile and run:
 *   javac -d out src/CreationalDemo.java
 *   java -cp out CreationalDemo
 */
public class CreationalDemo {

    public static void main(String[] args) {
        section("Factory Method: the caller names WHAT it wants, never HOW it is built");
        factoryMethod();

        section("Abstract Factory: one choice fixes a whole consistent family");
        abstractFactory();

        section("Why this is not just a switch statement in disguise");
        openClosedCheck();
    }

    // ------------------------------------------------ Factory Method

    interface PaymentProcessor {
        String charge(long amountCents);
    }

    static final class StripeProcessor implements PaymentProcessor {
        @Override
        public String charge(long amountCents) {
            return "Stripe charged " + amountCents + " cents (PaymentIntent API)";
        }
    }

    static final class AdyenProcessor implements PaymentProcessor {
        @Override
        public String charge(long amountCents) {
            return "Adyen charged " + amountCents + " cents (Payments API)";
        }
    }

    /**
     * The factory method. Callers depend on the PaymentProcessor interface and
     * on this one selection point -- not on any concrete provider class.
     */
    static PaymentProcessor processorFor(String providerKey) {
        return switch (providerKey) {
            case "stripe" -> new StripeProcessor();
            case "adyen" -> new AdyenProcessor();
            default -> throw new IllegalArgumentException("Unknown payment provider: " + providerKey);
        };
    }

    private static void factoryMethod() {
        for (String key : new String[]{"stripe", "adyen"}) {
            System.out.println("processorFor(\"" + key + "\").charge(2500) -> " + processorFor(key).charge(2500));
        }
        try {
            processorFor("paypal");
        } catch (IllegalArgumentException e) {
            System.out.println("processorFor(\"paypal\") -> " + e.getClass().getSimpleName()
                    + ": " + e.getMessage());
        }
        System.out.println("The call site never mentions StripeProcessor or AdyenProcessor by name.");
    }

    // ------------------------------------------------ Abstract Factory

    interface TaxCalculator {
        long taxFor(long amountCents);
    }

    interface InvoiceFormatter {
        String format(long amountCents, long taxCents);
    }

    /** The abstract factory: one object that produces a matched SET of products. */
    interface RegionFactory {
        TaxCalculator taxCalculator();

        InvoiceFormatter invoiceFormatter();
    }

    static final class GermanyFactory implements RegionFactory {
        @Override
        public TaxCalculator taxCalculator() {
            return amount -> Math.round(amount * 0.19); // 19% VAT
        }

        @Override
        public InvoiceFormatter invoiceFormatter() {
            return (amount, tax) -> "Rechnung: %d,%02d EUR (davon %d,%02d EUR MwSt.)"
                    .formatted(amount / 100, amount % 100, tax / 100, tax % 100);
        }
    }

    static final class UnitedStatesFactory implements RegionFactory {
        @Override
        public TaxCalculator taxCalculator() {
            return amount -> Math.round(amount * 0.0875); // 8.75% sales tax
        }

        @Override
        public InvoiceFormatter invoiceFormatter() {
            return (amount, tax) -> "Invoice: $%d.%02d (incl. $%d.%02d sales tax)"
                    .formatted(amount / 100, amount % 100, tax / 100, tax % 100);
        }
    }

    private static final Map<String, RegionFactory> REGIONS = Map.of(
            "DE", new GermanyFactory(),
            "US", new UnitedStatesFactory());

    private static void abstractFactory() {
        long amount = 10_000;
        for (String region : new String[]{"DE", "US"}) {
            RegionFactory factory = REGIONS.get(region);
            long tax = factory.taxCalculator().taxFor(amount);
            System.out.println(region + ": " + factory.invoiceFormatter().format(amount, tax));
        }
        System.out.println("Picking the factory once guarantees the tax rule and the invoice wording");
        System.out.println("always come from the same region -- a mismatched pair is unrepresentable.");
    }

    // ------------------------------------------------ Design check

    private static void openClosedCheck() {
        System.out.println("Adding a third provider touches exactly one method (processorFor).");
        System.out.println("Adding a third region touches exactly one map (REGIONS) plus a new factory class.");
        System.out.println("Every consumer of PaymentProcessor / TaxCalculator / InvoiceFormatter is untouched.");
        System.out.println("That single-point-of-change property is the pattern's actual payoff --");
        System.out.println("not the existence of a class with 'Factory' in its name.");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
