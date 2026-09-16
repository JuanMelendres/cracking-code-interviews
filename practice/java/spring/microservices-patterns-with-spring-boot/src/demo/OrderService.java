package demo;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.function.Supplier;

/** A real Spring-managed service that calls another microservice
 * (payment-service, stood in by {@link FlakyPaymentServer}) through a
 * declarative HTTP client, with the call wrapped in a real Resilience4j
 * CircuitBreaker. This is the programmatic decoration API --
 * {@code @CircuitBreaker(name = "payment")} on the method is the
 * annotation-driven equivalent (Spring AOP proxies the method and calls
 * this exact same {@code decorateSupplier}/{@code executeSupplier} path
 * underneath), requiring the resilience4j-spring-boot3 starter's AOP
 * aspect instead of this pack's plain resilience4j-circuitbreaker jar. */
@Service
public class OrderService {

    private final PaymentClient paymentClient;
    private final CircuitBreaker circuitBreaker;

    public OrderService(PaymentClient paymentClient, @Qualifier("payment") CircuitBreaker circuitBreaker) {
        this.paymentClient = paymentClient;
        this.circuitBreaker = circuitBreaker;
    }

    public ChargeResult charge(String orderId, double amount) {
        Supplier<ChargeResponse> call = () -> paymentClient.charge(new ChargeRequest(orderId, amount));
        Supplier<ChargeResponse> decorated = CircuitBreaker.decorateSupplier(circuitBreaker, call);
        try {
            ChargeResponse response = decorated.get();
            return new ChargeResult(true, response.status(), null);
        } catch (CallNotPermittedException e) {
            // The breaker is OPEN -- this call was rejected without ever
            // reaching the network. This is the fallback path.
            return new ChargeResult(false, null, "circuit-open: payment service unavailable, try again shortly");
        } catch (RestClientException e) {
            return new ChargeResult(false, null, "payment call failed: " + e.getClass().getSimpleName());
        }
    }

    public record ChargeResult(boolean success, String status, String fallbackReason) {
    }
}
