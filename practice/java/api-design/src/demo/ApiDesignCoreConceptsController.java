package demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/** One controller, four real gaps closed:
 * <ol>
 *   <li>HATEOAS + Richardson Maturity Model -- {@link #getOrder} returns
 *       state-dependent {@code _links}; {@link #rpc} contrasts a
 *       Level-0 single-endpoint/action-field shape against it.</li>
 *   <li>RFC 9457 Problem Details -- {@link #handleNotFound} returns a
 *       real {@link ProblemDetail}, Spring 6's built-in type.</li>
 *   <li>Filtering and sorting query parameters -- {@link #listOrders}.</li>
 *   <li>Bulk operations with per-item partial success -- {@link #bulkCreate}.</li>
 * </ol> */
@RestController
public class ApiDesignCoreConceptsController {

    private static final Map<Long, Order> ORDERS = new LinkedHashMap<>();
    private static final AtomicLong NEXT_ID = new AtomicLong(100);

    static {
        ORDERS.put(1L, new Order(1L, OrderStatus.PENDING, 50.00));
        ORDERS.put(2L, new Order(2L, OrderStatus.DELIVERED, 120.00));
        ORDERS.put(3L, new Order(3L, OrderStatus.PENDING, 75.00));
        ORDERS.put(4L, new Order(4L, OrderStatus.SHIPPED, 200.00));
    }

    // --- HATEOAS: a real resource whose _links change with its own state ---

    @GetMapping("/orders/{id}")
    public OrderResource getOrder(@PathVariable long id) {
        return new OrderResource(getOrderOrThrow(id));
    }

    // --- Level-0 RPC contrast: one URI, one verb, an "action" field standing in for both ---

    @PostMapping("/rpc")
    public Object rpc(@RequestBody RpcRequest request) {
        if ("getOrder".equals(request.action)) {
            return new OrderResource(getOrderOrThrow(request.id));
        }
        throw new IllegalArgumentException("unknown action: " + request.action);
    }

    // --- Filtering and sorting via query parameters ---

    @GetMapping("/orders")
    public List<OrderResource> listOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort) {

        List<Order> result = new ArrayList<>(ORDERS.values());

        if (status != null) {
            OrderStatus filterStatus = OrderStatus.valueOf(status.toUpperCase());
            result.removeIf(o -> o.status != filterStatus);
        }

        if (sort != null) {
            String[] parts = sort.split(",");
            String field = parts[0];
            boolean desc = parts.length > 1 && "desc".equalsIgnoreCase(parts[1]);
            Comparator<Order> comparator = switch (field) {
                case "amount" -> Comparator.comparingDouble(o -> o.amount);
                case "id" -> Comparator.comparingLong(o -> o.id);
                default -> throw new IllegalArgumentException("unsupported sort field: " + field);
            };
            if (desc) comparator = comparator.reversed();
            result.sort(comparator);
        }

        return result.stream().map(OrderResource::new).toList();
    }

    // --- Bulk create: per-item partial success, not one all-or-nothing status code ---

    @PostMapping("/orders/bulk")
    public ResponseEntity<List<BulkItemResult>> bulkCreate(@RequestBody List<BulkCreateItem> items) {
        List<BulkItemResult> results = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            BulkCreateItem item = items.get(i);
            if (item.amount <= 0) {
                results.add(BulkItemResult.failed(i, "amount must be positive"));
                continue;
            }
            long newId = NEXT_ID.getAndIncrement();
            ORDERS.put(newId, new Order(newId, OrderStatus.PENDING, item.amount));
            results.add(BulkItemResult.ok(i, newId));
        }
        return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(results);
    }

    // --- RFC 9457 Problem Details ---

    @ExceptionHandler(OrderNotFoundException.class)
    public ProblemDetail handleNotFound(OrderNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Order Not Found");
        problem.setType(URI.create("https://api.example.com/errors/order-not-found"));
        problem.setInstance(URI.create("/orders/" + ex.orderId));
        problem.setProperty("errorCode", "ORDER_NOT_FOUND");
        return problem;
    }

    private Order getOrderOrThrow(long id) {
        Order order = ORDERS.get(id);
        if (order == null) throw new OrderNotFoundException(id);
        return order;
    }
}
