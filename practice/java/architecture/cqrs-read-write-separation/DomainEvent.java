import java.math.BigDecimal;

/** Events published by the command side. The projector consumes these to build the read model. */
sealed interface DomainEvent permits OrderCreated, ItemAdded, OrderCompleted {
    long orderId();
    long publishedAtNanos();
}

record OrderCreated(long orderId, String customerId, long publishedAtNanos) implements DomainEvent {
}

record ItemAdded(long orderId, String sku, int quantity, BigDecimal unitPrice, long publishedAtNanos)
        implements DomainEvent {
}

record OrderCompleted(long orderId, long publishedAtNanos) implements DomainEvent {
}
