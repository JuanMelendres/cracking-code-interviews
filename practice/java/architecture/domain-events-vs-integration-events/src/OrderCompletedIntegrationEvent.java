import java.util.HashMap;
import java.util.Map;

/**
 * The stable, versioned PUBLIC contract external consumers actually
 * depend on. Its shape is deliberately decoupled from whatever the write
 * side's internal domain event looks like this month -- that decoupling
 * is the entire point of this chapter.
 */
public class OrderCompletedIntegrationEvent {

    public static final String CONTRACT_VERSION = "order.completed.v1";

    public final String orderId;
    public final String totalAmount; // always a formatted string, always present, name never changes
    public final String customerId;

    public OrderCompletedIntegrationEvent(String orderId, String totalAmount, String customerId) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.customerId = customerId;
    }

    public Map<String, Object> publishAsWireMessage() {
        Map<String, Object> wire = new HashMap<>();
        wire.put("contractVersion", CONTRACT_VERSION);
        wire.put("orderId", orderId);
        wire.put("totalAmount", totalAmount);
        wire.put("customerId", customerId);
        return wire;
    }
}
