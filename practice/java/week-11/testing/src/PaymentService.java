/** T-1101/T-1103 -- the unit under test. Retries a flaky gateway call up to
 * maxAttempts times (reusing Week 10's resilience vocabulary), with no
 * network, no database, no real dependency at all -- which is exactly
 * what makes it fast and appropriate to test with a mocked PaymentGateway. */
public class PaymentService {
    private final PaymentGateway gateway;
    private final int maxAttempts;

    public PaymentService(PaymentGateway gateway, int maxAttempts) {
        this.gateway = gateway;
        this.maxAttempts = maxAttempts;
    }

    public boolean processPayment(String customerId, long amountCents) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                gateway.charge(customerId, amountCents);
                return true;
            } catch (Exception e) {
                if (attempt == maxAttempts) return false;
            }
        }
        return false;
    }
}
