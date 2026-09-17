package demo;

import java.math.BigDecimal;

/** The other real-world DTO shape -- an *input* DTO, structurally
 * different from both the entity and the response DTO: no {@code id}
 * (the server assigns it), no {@code createdAt} (the server sets it),
 * and {@code customerId} rather than a full {@link Customer} (the
 * client only knows which customer by id, not the full related object). */
public record CreateOrderRequest(Long customerId, BigDecimal totalAmount) {
}
