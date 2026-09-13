package demo;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final Map<Long, OrderRequest> orders = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    // @Valid triggers Bean Validation on OrderRequest BEFORE this method body
    // ever runs -- if validation fails, Spring throws MethodArgumentNotValidException
    // and this method is never invoked at all (verified in the real transcript).
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody OrderRequest request) {
        long id = nextId.getAndIncrement();
        orders.put(id, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("orderId", id, "customerEmail", request.customerEmail()));
    }

    @GetMapping("/{id}")
    public OrderRequest get(@PathVariable("id") long id) {
        if (id == 999) {
            // Simulates a real, unexpected internal failure containing a
            // sensitive detail that must NEVER reach the client directly.
            throw new RuntimeException("unexpected DB failure: connection string postgres://admin:hunter2@internal-db:5432/orders");
        }
        OrderRequest order = orders.get(id);
        if (order == null) {
            throw new OrderNotFoundException(id);
        }
        return order;
    }
}
