package demo;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Specifications compose predicates at runtime -- unlike a derived query
 * method (fixed at compile time by its own name), the same Specification
 * objects can be combined, or left out, depending on which filters the
 * caller actually supplied. This is the tool for "N optional filter
 * parameters" screens that derived methods and static @Query cannot
 * express without an explosion of overloads.
 */
public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Order> minTotal(BigDecimal min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("totalAmount"), min);
    }
}
