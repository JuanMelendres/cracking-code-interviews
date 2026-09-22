import java.util.HashMap;
import java.util.Map;

/**
 * The write-side's own internal domain event, BEFORE an internal refactor.
 * This shape is free to change whenever the domain model changes -- that is
 * exactly what a domain event is for.
 */
public class OrderCompletedDomainEventV1 {

    public final String orderId;
    public final double total;
    public final String customerId;

    public OrderCompletedDomainEventV1(String orderId, double total, String customerId) {
        this.orderId = orderId;
        this.total = total;
        this.customerId = customerId;
    }

    /**
     * The UNSAFE path: publishing the domain event's own internal fields
     * directly onto the wire, as if the internal shape WERE the public
     * contract.
     */
    public Map<String, Object> publishDirectlyAsWireMessage() {
        Map<String, Object> wire = new HashMap<>();
        wire.put("orderId", orderId);
        wire.put("total", total);
        wire.put("customerId", customerId);
        return wire;
    }
}
