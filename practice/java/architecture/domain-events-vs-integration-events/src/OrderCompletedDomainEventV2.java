import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * The SAME real-world business event -- an order completed -- AFTER a
 * genuine, well-motivated internal refactor: `total` (a primitive double,
 * a real precision risk for money) became `grandTotal` (a BigDecimal,
 * fixing that risk), and a new internal-only field was added. Nothing
 * about the real-world business fact changed -- only the write side's own
 * internal representation of it did, exactly the kind of change a domain
 * model should be free to make.
 */
public class OrderCompletedDomainEventV2 {

    public final String orderId;
    public final BigDecimal grandTotal;
    public final String customerId;
    public final int loyaltyPointsEarned; // new internal-only field, no external consumer needs this

    public OrderCompletedDomainEventV2(String orderId, BigDecimal grandTotal, String customerId,
                                        int loyaltyPointsEarned) {
        this.orderId = orderId;
        this.grandTotal = grandTotal;
        this.customerId = customerId;
        this.loyaltyPointsEarned = loyaltyPointsEarned;
    }

    /**
     * The UNSAFE path again, now against the refactored internal shape --
     * the wire message's field names/types change right along with the
     * internal model, because there was never a translation boundary.
     */
    public Map<String, Object> publishDirectlyAsWireMessage() {
        Map<String, Object> wire = new HashMap<>();
        wire.put("orderId", orderId);
        wire.put("grandTotal", grandTotal); // renamed from "total" -- a real, honest refactor
        wire.put("customerId", customerId);
        wire.put("loyaltyPointsEarned", loyaltyPointsEarned);
        return wire;
    }
}
