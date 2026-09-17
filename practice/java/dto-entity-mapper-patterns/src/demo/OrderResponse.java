package demo;

import java.math.BigDecimal;
import java.time.Instant;

/** The API-facing shape (a "DTO," here implemented as a Java record) --
 * a real, different shape from {@link OrderEntity}, not a renamed copy:
 * {@code customerName} is flattened from the related Customer entity,
 * and there is no field at all for the entity's internal fraud-score
 * notes -- that field is structurally impossible to serialize by
 * accident because this record never declared it. */
public record OrderResponse(Long id, String customerName, BigDecimal totalAmount, Instant createdAt) {
}
