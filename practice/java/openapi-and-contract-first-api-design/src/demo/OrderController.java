package demo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final Map<Long, OrderDto> store = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public OrderController() {
        store.put(1L, new OrderDto(1L, "Ada Lovelace", 1999));
    }

    @Operation(summary = "Fetch a single order by ID")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "404", description = "No order with that ID")
    @GetMapping("/{id}")
    public OrderDto getOrder(@PathVariable long id) {
        OrderDto order = store.get(id);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "order " + id + " not found");
        }
        return order;
    }

    @Operation(summary = "Create a new order")
    @ApiResponse(responseCode = "201", description = "Order created")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderDto createOrder(@RequestBody CreateOrderRequest request) {
        long id = nextId.getAndIncrement();
        OrderDto order = new OrderDto(id, request.customerName, request.amountCents);
        store.put(id, order);
        return order;
    }
}
