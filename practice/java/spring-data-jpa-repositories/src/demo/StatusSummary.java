package demo;

import java.math.BigDecimal;

/**
 * Interface-based projection. Spring Data binds each accessor to the
 * matching column alias in the native query's result set -- no DTO
 * constructor, no manual ResultSet mapping.
 */
public interface StatusSummary {
    String getStatus();

    Long getOrderCount();

    BigDecimal getTotal();
}
