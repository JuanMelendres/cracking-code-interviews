package demo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    // Derived query method: Spring Data parses the method name into a
    // property tree (status, totalAmount) against Order's own JPA metamodel
    // and builds the query -- no JPQL/SQL written by hand.
    List<Order> findByStatusAndTotalAmountGreaterThan(OrderStatus status, BigDecimal amount, Sort sort);

    // Derived query method + Pageable: same name-parsing mechanism, but the
    // query is executed twice under the hood -- once for the page of
    // content, once (COUNT) for Page#getTotalElements().
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // @Query with JPQL: operates against the entity model (Order, its
    // items association), not raw tables -- DISTINCT here is necessary
    // because the join fans out one row per matching OrderItem.
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.sku = :sku")
    List<Order> findOrdersContainingSku(@Param("sku") String sku);

    // @Query with a native query: raw SQL against the actual orders table,
    // bypassing JPQL/HQL translation entirely. Column aliases (status,
    // order_count, total) are bound to the StatusSummary projection's
    // accessor names by Spring Data's own convention (case-insensitive,
    // underscore-to-camelCase).
    @Query(value = "SELECT status AS status, COUNT(*) AS order_count, SUM(total_amount) AS total " +
            "FROM orders GROUP BY status ORDER BY status", nativeQuery = true)
    List<StatusSummary> statusSummaryNative();
}
