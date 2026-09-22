import java.math.BigDecimal;
import java.util.Map;

/**
 * Real, deterministic proof (no timing, no randomness -- every run produces
 * an identical result) that publishing a domain event directly as a public
 * contract breaks a downstream consumer the moment an internal, otherwise
 * harmless refactor happens -- while translating through a stable
 * integration event does not.
 */
public class DomainVsIntegrationEventDemo {

    public static void main(String[] args) {
        NotificationConsumer consumer = new NotificationConsumer();

        System.out.println("=== UNSAFE: publishing the domain event directly as the wire message ===");

        OrderCompletedDomainEventV1 domainEventV1 = new OrderCompletedDomainEventV1("order-123", 49.99, "cust-1");
        Map<String, Object> unsafeWireV1 = domainEventV1.publishDirectlyAsWireMessage();
        System.out.println("  Before refactor, consumer reads field \"total\": "
                + consumer.consumeExpectingField(unsafeWireV1, "total"));

        System.out.println();
        System.out.println("  --- Internal refactor happens: double total -> BigDecimal grandTotal ---");
        System.out.println("  --- (a real, well-motivated internal fix -- primitives are a precision risk for money) ---");
        System.out.println();

        OrderCompletedDomainEventV2 domainEventV2 =
                new OrderCompletedDomainEventV2("order-124", new BigDecimal("49.99"), "cust-1", 50);
        Map<String, Object> unsafeWireV2 = domainEventV2.publishDirectlyAsWireMessage();
        System.out.println("  After refactor, consumer STILL asks for field \"total\" (its contract never changed):");
        try {
            consumer.consumeExpectingField(unsafeWireV2, "total");
            System.out.println("  UNEXPECTED: this should have failed.");
        } catch (IllegalStateException e) {
            System.out.println("  BROKEN, exactly as expected: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== SAFE: translating through a stable OrderCompletedIntegrationEvent contract ===");

        Map<String, Object> safeWireV1 = IntegrationEventTranslator.translate(domainEventV1).publishAsWireMessage();
        System.out.println("  Before refactor, consumer reads field \"totalAmount\": "
                + consumer.consumeExpectingField(safeWireV1, "totalAmount"));

        Map<String, Object> safeWireV2 = IntegrationEventTranslator.translate(domainEventV2).publishAsWireMessage();
        System.out.println("  After the SAME internal refactor, consumer reads field \"totalAmount\": "
                + consumer.consumeExpectingField(safeWireV2, "totalAmount"));

        System.out.println();
        System.out.println("=== Result ===");
        System.out.println("  The direct-publish consumer broke the instant an internal-only refactor happened.");
        System.out.println("  The translated-contract consumer never noticed the same refactor at all --");
        System.out.println("  its field, \"totalAmount\", was identical before and after (" + safeWireV1.get("totalAmount")
                + " both times), because the translation boundary absorbed the internal change.");
    }
}
