import java.math.BigDecimal;

/**
 * The read side. Deliberately flattened and denormalized: everything a "show me this order"
 * query needs, in one object, with no joins across an items table required. Shaped for the
 * query, not for the write-side's normalized invariants.
 */
final class OrderSummaryView {
    final long orderId;
    final String customerId;
    volatile int totalItemCount;
    volatile BigDecimal totalAmount = BigDecimal.ZERO;
    volatile String status = "OPEN";
    volatile long lastUpdatedAtNanos;

    OrderSummaryView(long orderId, String customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }
}
