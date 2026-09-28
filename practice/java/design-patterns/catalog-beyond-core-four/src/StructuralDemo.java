import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Adapter, Proxy, and Facade, executed against realistic backend shapes: a
 * legacy SDK that does not match the interface our code wants, a dynamic proxy
 * that adds retries without touching the implementation, and a facade over a
 * multi-step checkout.
 *
 * Java 21. Pure JDK, no dependencies.
 *
 * Compile and run:
 *   javac -d out src/StructuralDemo.java
 *   java -cp out StructuralDemo
 */
public class StructuralDemo {

    public static void main(String[] args) {
        section("Adapter: making an unchangeable third-party shape fit our interface");
        adapter();

        section("Proxy: adding retry behaviour with zero changes to the implementation");
        proxy();

        section("Facade: one call that hides a four-step orchestration");
        facade();
    }

    // ------------------------------------------------ Adapter

    /** The interface our application code is written against. */
    interface GeoLookup {
        String countryOf(String ipAddress);
    }

    /** A third-party SDK class we cannot modify: wrong method name, wrong return type. */
    static final class LegacyGeoSdk {
        Object[] resolve(String ip) {
            return new Object[]{"DE", "Berlin", 52.52, 13.405};
        }
    }

    /** The adapter: owns the translation, so nothing else in the codebase sees Object[]. */
    static final class LegacyGeoAdapter implements GeoLookup {
        private final LegacyGeoSdk sdk;

        LegacyGeoAdapter(LegacyGeoSdk sdk) {
            this.sdk = sdk;
        }

        @Override
        public String countryOf(String ipAddress) {
            Object[] raw = sdk.resolve(ipAddress);
            if (raw.length == 0 || !(raw[0] instanceof String country)) {
                throw new IllegalStateException("Legacy SDK returned an unusable shape");
            }
            return country;
        }
    }

    private static void adapter() {
        GeoLookup lookup = new LegacyGeoAdapter(new LegacyGeoSdk());
        System.out.println("lookup.countryOf(\"203.0.113.7\") = " + lookup.countryOf("203.0.113.7"));
        System.out.println("The Object[] unpacking exists in exactly one class, not at every call site.");
        System.out.println("Swapping the SDK later means rewriting one adapter, not the application.");
    }

    // ------------------------------------------------ Proxy

    interface InventoryClient {
        int stockFor(String sku);
    }

    /** Fails a fixed number of times, then succeeds -- a real, deterministic flaky dependency. */
    static final class FlakyInventoryClient implements InventoryClient {
        private final AtomicInteger calls = new AtomicInteger();
        private final int failuresBeforeSuccess;

        FlakyInventoryClient(int failuresBeforeSuccess) {
            this.failuresBeforeSuccess = failuresBeforeSuccess;
        }

        @Override
        public int stockFor(String sku) {
            int attempt = calls.incrementAndGet();
            if (attempt <= failuresBeforeSuccess) {
                throw new IllegalStateException("transient upstream failure on attempt " + attempt);
            }
            return 42;
        }

        int callCount() {
            return calls.get();
        }
    }

    /** A JDK dynamic proxy: retry logic applied to every method of the interface. */
    @SuppressWarnings("unchecked")
    static <T> T withRetries(Class<T> contract, T target, int maxAttempts, List<String> log) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxyInstance, Method method, Object[] arguments) throws Throwable {
                RuntimeException last = null;
                for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                    try {
                        Object result = method.invoke(target, arguments);
                        log.add(method.getName() + " succeeded on attempt " + attempt);
                        return result;
                    } catch (InvocationTargetException e) {
                        Throwable cause = e.getCause();
                        if (!(cause instanceof RuntimeException runtime)) {
                            throw cause;
                        }
                        last = runtime;
                        log.add(method.getName() + " failed on attempt " + attempt + ": " + cause.getMessage());
                    }
                }
                throw last;
            }
        };
        return (T) Proxy.newProxyInstance(contract.getClassLoader(), new Class<?>[]{contract}, handler);
    }

    private static void proxy() {
        List<String> log = new ArrayList<>();
        FlakyInventoryClient real = new FlakyInventoryClient(2);
        InventoryClient guarded = withRetries(InventoryClient.class, real, 4, log);

        System.out.println("guarded.stockFor(\"SKU-1\") = " + guarded.stockFor("SKU-1"));
        log.forEach(line -> System.out.println("  " + line));
        System.out.println("Real underlying calls made: " + real.callCount());
        System.out.println("Proxy runtime class: " + guarded.getClass().getName());
        System.out.println("FlakyInventoryClient contains no retry code at all.");

        System.out.println();
        List<String> failLog = new ArrayList<>();
        InventoryClient tooFewAttempts = withRetries(
                InventoryClient.class, new FlakyInventoryClient(5), 2, failLog);
        try {
            tooFewAttempts.stockFor("SKU-2");
        } catch (IllegalStateException e) {
            System.out.println("With maxAttempts=2 against 5 failures, the last error propagates: "
                    + e.getMessage());
        }
    }

    // ------------------------------------------------ Facade

    static final class PricingService {
        long priceCents(String sku, int quantity) {
            return 1_999L * quantity;
        }
    }

    static final class TaxService {
        long taxCents(long subtotal) {
            return Math.round(subtotal * 0.21);
        }
    }

    static final class ReservationService {
        String reserve(String sku, int quantity) {
            return "RES-" + sku + "-" + quantity;
        }
    }

    static final class ReceiptService {
        String render(String reservationId, long subtotal, long tax) {
            return "%s | subtotal %d.%02d | tax %d.%02d | total %d.%02d".formatted(
                    reservationId, subtotal / 100, subtotal % 100,
                    tax / 100, tax % 100,
                    (subtotal + tax) / 100, (subtotal + tax) % 100);
        }
    }

    /** The facade: one entry point, four collaborators, one correct call order. */
    static final class CheckoutFacade {
        private final PricingService pricing = new PricingService();
        private final TaxService tax = new TaxService();
        private final ReservationService reservations = new ReservationService();
        private final ReceiptService receipts = new ReceiptService();

        String checkout(String sku, int quantity) {
            long subtotal = pricing.priceCents(sku, quantity);
            long taxDue = tax.taxCents(subtotal);
            String reservationId = reservations.reserve(sku, quantity);
            return receipts.render(reservationId, subtotal, taxDue);
        }
    }

    private static void facade() {
        System.out.println("new CheckoutFacade().checkout(\"SKU-1\", 3):");
        System.out.println("  " + new CheckoutFacade().checkout("SKU-1", 3));
        System.out.println("Callers cannot get the order wrong (reserve before pricing, tax on the wrong base)");
        System.out.println("because the ordering lives in one place. The four services stay independently usable.");
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
