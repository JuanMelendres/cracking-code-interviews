package demo;

import org.junit.jupiter.api.Test;

import java.lang.reflect.RecordComponent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/** Real MapStruct-generated code under test -- {@code new OrderMapperImpl()}
 * below is not hand-written anywhere in this pack's own source; it was
 * produced by MapStruct's real annotation processor at compile time (see
 * generated-sources/demo/OrderMapperImpl.java, captured verbatim in this
 * pack's README). */
class OrderMapperTest {

    private final OrderMapper mapper = new OrderMapperImpl();

    @Test
    void realMapper_flattensCustomerAndDropsInternalField() {
        Customer customer = new Customer(42L, "Ada Lovelace");
        OrderEntity entity = new OrderEntity(
                1L, customer, new BigDecimal("199.99"), Instant.parse("2026-09-16T10:00:00Z"),
                "FLAGGED: 3 chargebacks in 90 days, manual review required");

        OrderResponse response = mapper.toResponse(entity);

        System.out.println("Real mapped response: " + response);

        assertEquals(1L, response.id());
        assertEquals("Ada Lovelace", response.customerName());
        assertEquals(new BigDecimal("199.99"), response.totalAmount());
        assertEquals(Instant.parse("2026-09-16T10:00:00Z"), response.createdAt());
    }

    @Test
    void realMapper_handlesNullCustomerWithoutNPE() {
        OrderEntity entity = new OrderEntity(2L, null, new BigDecimal("50.00"), Instant.now(), "clean");

        OrderResponse response = mapper.toResponse(entity);

        System.out.println("Real mapped response with null customer: " + response);
        assertNull(response.customerName());
    }

    @Test
    void orderResponse_hasNoFieldForTheSensitiveInternalNote_compileTimeProof() {
        // Not a runtime check that the mapper "forgot" to copy the field --
        // a real reflective proof that OrderResponse, as a record, has no
        // component for internalFraudScoreNotes at all. The leak is
        // structurally impossible, not merely unexercised.
        RecordComponent[] components = OrderResponse.class.getRecordComponents();
        String[] names = Arrays.stream(components).map(RecordComponent::getName).toArray(String[]::new);

        System.out.println("Real OrderResponse record components: " + Arrays.toString(names));

        boolean hasSensitiveField = Arrays.stream(names)
                .anyMatch(n -> n.toLowerCase().contains("fraud"));
        assertFalse(hasSensitiveField, "OrderResponse must have no component related to the internal fraud notes");
        assertEquals(4, components.length, "OrderResponse must expose exactly its 4 declared fields, nothing more");
    }
}
