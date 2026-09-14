package demo.broken;

import demo.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Deliberately broken: Order has no "totlAmount" property (the real field
 * is "totalAmount"). Spring Data still compiles this interface fine --
 * property names are validated at repository-proxy creation time, not by
 * javac -- so the failure only surfaces when the ApplicationContext boots.
 */
public interface BrokenOrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByTotlAmountGreaterThan(BigDecimal amount);
}
