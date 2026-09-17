package demo;

import java.math.BigDecimal;
import java.time.Instant;

/** The persistence-shaped object -- what a real JPA @Entity would look
 * like (annotations omitted here on purpose; see
 * jpa-entity-lifecycle-and-the-n1-problem.md for the real Hibernate
 * lifecycle). Two things about this shape that make it a bad fit to
 * expose directly over an API:
 * 1. {@code customer} is a full related-entity reference, not a flat
 *    value -- serializing it directly risks dragging in lazy-loading
 *    machinery or the entire Customer graph.
 * 2. {@code internalFraudScoreNotes} is real, sensitive internal data
 *    that must never reach a client. */
public class OrderEntity {
    private Long id;
    private Customer customer;
    private BigDecimal totalAmount;
    private Instant createdAt;
    private String internalFraudScoreNotes;

    public OrderEntity(Long id, Customer customer, BigDecimal totalAmount, Instant createdAt, String internalFraudScoreNotes) {
        this.id = id;
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.internalFraudScoreNotes = internalFraudScoreNotes;
    }

    public Long getId() { return id; }
    public Customer getCustomer() { return customer; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public String getInternalFraudScoreNotes() { return internalFraudScoreNotes; }
}
