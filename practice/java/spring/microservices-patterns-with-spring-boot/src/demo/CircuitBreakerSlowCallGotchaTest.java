package demo;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The genuine gotcha this pack set out to find: does a default-configured
 * CircuitBreaker protect you from a downstream that's merely SLOW but
 * still returns 200? Answer, verified with two real breakers against the
 * same real slow server: no. failureRateThreshold only counts thrown
 * exceptions/error responses as failures -- a successful-but-slow call is
 * not a failure unless slowCallDurationThreshold/slowCallRateThreshold
 * are explicitly configured. */
class CircuitBreakerSlowCallGotchaTest {

    private static final int SLOW_MILLIS = 300;

    @Test
    void defaultConfigIgnoresSlowSuccessfulCalls() throws Exception {
        FlakyPaymentServer server = new FlakyPaymentServer(SLOW_MILLIS);
        server.start();
        server.setMode(FlakyPaymentServer.Mode.SLOW);
        String baseUrl = "http://localhost:" + server.port();

        CircuitBreakerConfig defaultConfig = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(5)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50)
                // deliberately NOT configuring slowCallDurationThreshold/slowCallRateThreshold
                .build();

        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("paymentClient", PaymentClient.class,
                () -> PaymentClientFactory.create(baseUrl, 5000));
        context.registerBean("payment", CircuitBreaker.class,
                () -> CircuitBreaker.of("payment-default", defaultConfig));
        context.registerBean(OrderService.class);
        context.refresh();

        OrderService orderService = context.getBean(OrderService.class);
        CircuitBreaker breaker = context.getBean("payment", CircuitBreaker.class);

        System.out.println("--- Default config: 5 real slow-but-successful calls (" + SLOW_MILLIS + "ms each) ---");
        for (int i = 1; i <= 5; i++) {
            long start = System.currentTimeMillis();
            OrderService.ChargeResult result = orderService.charge("slow-default-" + i, 10.00);
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Call " + i + " (" + elapsed + "ms): " + result + " | breaker state=" + breaker.getState());
            assertTrue(result.success(), "the call itself must succeed -- the server returns 200, just slowly");
        }

        System.out.println("Default-config breaker final state: " + breaker.getState());
        assertEquals(CircuitBreaker.State.CLOSED, breaker.getState(),
                "a breaker with no slow-call thresholds does NOT trip on slow-but-successful calls");

        context.close();
        server.stop();
    }

    @Test
    void slowCallThresholdConfigDoesTripOnTheSameRealSlowServer() throws Exception {
        FlakyPaymentServer server = new FlakyPaymentServer(SLOW_MILLIS);
        server.start();
        server.setMode(FlakyPaymentServer.Mode.SLOW);
        String baseUrl = "http://localhost:" + server.port();

        CircuitBreakerConfig slowAwareConfig = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(5)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50)
                .slowCallDurationThreshold(Duration.ofMillis(200))
                .slowCallRateThreshold(50)
                .build();

        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("paymentClient", PaymentClient.class,
                () -> PaymentClientFactory.create(baseUrl, 5000));
        context.registerBean("payment", CircuitBreaker.class,
                () -> CircuitBreaker.of("payment-slow-aware", slowAwareConfig));
        context.registerBean(OrderService.class);
        context.refresh();

        OrderService orderService = context.getBean(OrderService.class);
        CircuitBreaker breaker = context.getBean("payment", CircuitBreaker.class);

        System.out.println("--- Slow-call-aware config: same real slow server, slowCallDurationThreshold=200ms ---");
        for (int i = 1; i <= 5; i++) {
            OrderService.ChargeResult result = orderService.charge("slow-aware-" + i, 10.00);
            System.out.println("Call " + i + ": " + result + " | breaker state=" + breaker.getState());
        }

        System.out.println("Slow-aware breaker final state: " + breaker.getState());
        assertEquals(CircuitBreaker.State.OPEN, breaker.getState(),
                "with slow-call thresholds configured, the same 300ms responses now count as failures");

        context.close();
        server.stop();
    }
}
