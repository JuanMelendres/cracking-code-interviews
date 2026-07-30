/** The dependency a unit test mocks -- an external, slow, potentially-flaky payment provider. */
public interface PaymentGateway {
    void charge(String customerId, long amountCents) throws Exception;
}
