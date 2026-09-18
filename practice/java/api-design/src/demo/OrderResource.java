package demo;

import java.util.LinkedHashMap;
import java.util.Map;

/** HAL-style representation: the order's own fields plus a real
 * "_links" map computed from its current state -- this is the level-3
 * (HATEOAS) piece of the Richardson Maturity Model, demonstrated, not
 * just defined. */
public class OrderResource {
    public final long id;
    public final OrderStatus status;
    public final double amount;
    public final Map<String, LinkInfo> _links = new LinkedHashMap<>();

    public OrderResource(Order order) {
        this.id = order.id;
        this.status = order.status;
        this.amount = order.amount;

        _links.put("self", new LinkInfo("/orders/" + order.id, "GET"));

        switch (order.status) {
            case PENDING -> {
                _links.put("cancel", new LinkInfo("/orders/" + order.id, "DELETE"));
                _links.put("ship", new LinkInfo("/orders/" + order.id + "/ship", "POST"));
            }
            case SHIPPED -> _links.put("deliver", new LinkInfo("/orders/" + order.id + "/deliver", "POST"));
            case DELIVERED -> _links.put("return", new LinkInfo("/orders/" + order.id + "/return", "POST"));
            case CANCELLED -> { /* terminal state -- no further links besides self */ }
        }
    }
}
