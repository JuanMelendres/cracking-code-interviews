package demo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;

public class GoodRepositoryDemo {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class)) {
            OrderRepository repo = ctx.getBean(OrderRepository.class);
            PlatformTransactionManager txm = ctx.getBean(PlatformTransactionManager.class);
            TransactionTemplate tx = new TransactionTemplate(txm);

            tx.executeWithoutResult(status -> {
                Order o1 = new Order("cust-1", OrderStatus.SHIPPED, new BigDecimal("120.00"));
                o1.addItem("SKU-KEYBOARD", 1);
                Order o2 = new Order("cust-2", OrderStatus.PENDING, new BigDecimal("45.50"));
                o2.addItem("SKU-MOUSE", 2);
                Order o3 = new Order("cust-1", OrderStatus.SHIPPED, new BigDecimal("300.00"));
                o3.addItem("SKU-KEYBOARD", 1);
                o3.addItem("SKU-MONITOR", 1);
                Order o4 = new Order("cust-3", OrderStatus.CANCELLED, new BigDecimal("15.00"));
                o4.addItem("SKU-CABLE", 3);
                repo.saveAll(List.of(o1, o2, o3, o4));
            });

            System.out.println();
            System.out.println("=== 1. Derived query method: findByStatusAndTotalAmountGreaterThan ===");
            System.out.println("(watch the Hibernate SQL log above this line -- Spring Data built it from the method name)");
            List<Order> shippedOver100 = tx.execute(status ->
                    repo.findByStatusAndTotalAmountGreaterThan(
                            OrderStatus.SHIPPED, new BigDecimal("100.00"), Sort.by("totalAmount").descending()));
            shippedOver100.forEach(o -> System.out.println("  -> " + o));

            System.out.println();
            System.out.println("=== 2. Derived query method + Pageable: findByStatus ===");
            Page<Order> page0 = tx.execute(status ->
                    repo.findByStatus(OrderStatus.SHIPPED, PageRequest.of(0, 1)));
            System.out.println("  page size=1: content.size()=" + page0.getContent().size()
                    + " totalElements=" + page0.getTotalElements()
                    + " totalPages=" + page0.getTotalPages()
                    + " (content is one row; totalElements came from a SEPARATE COUNT query)");

            System.out.println();
            System.out.println("=== 3. @Query JPQL: findOrdersContainingSku (entity join) ===");
            List<Order> withKeyboard = tx.execute(status -> repo.findOrdersContainingSku("SKU-KEYBOARD"));
            withKeyboard.forEach(o -> System.out.println("  -> " + o));

            System.out.println();
            System.out.println("=== 4. @Query native: statusSummaryNative (raw SQL + interface projection) ===");
            List<StatusSummary> summary = tx.execute(status -> repo.statusSummaryNative());
            summary.forEach(s -> System.out.println("  -> status=" + s.getStatus()
                    + " orderCount=" + s.getOrderCount() + " total=" + s.getTotal()));

            System.out.println();
            System.out.println("=== 5. Specification: dynamic predicate composition ===");
            long countNoFilter = tx.execute(status -> repo.count((root, query, cb) -> null));
            System.out.println("  no filters                          -> " + countNoFilter + " orders");

            Specification<Order> onlyShipped = OrderSpecifications.hasStatus(OrderStatus.SHIPPED);
            long countShipped = tx.execute(status -> repo.count(onlyShipped));
            System.out.println("  status=SHIPPED                      -> " + countShipped + " orders");

            Specification<Order> shippedAndBig = onlyShipped.and(OrderSpecifications.minTotal(new BigDecimal("200.00")));
            long countShippedAndBig = tx.execute(status -> repo.count(shippedAndBig));
            System.out.println("  status=SHIPPED AND total>=200.00    -> " + countShippedAndBig + " orders");
        }
    }
}
