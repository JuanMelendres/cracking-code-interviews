package demo;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Real, executed proof that a Resilience4j CircuitBreaker -- wired as a
 * genuine Spring bean, called through a real {@code @HttpExchange}
 * client, against a real local HTTP server -- walks through
 * CLOSED -> OPEN -> HALF_OPEN -> CLOSED exactly as documented, driven by
 * real request failures and real elapsed time, not a mocked state
 * machine. */
class CircuitBreakerFailureTrippingTest {

    private FlakyPaymentServer server;
    private GenericApplicationContext context;
    private OrderService orderService;
    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() throws Exception {
        server = new FlakyPaymentServer(0);
        server.start();
        String baseUrl = "http://localhost:" + server.port();

        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(5)
                .minimumNumberOfCalls(5)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(2))
                .permittedNumberOfCallsInHalfOpenState(2)
                .build();

        context = new GenericApplicationContext();
        context.registerBean("paymentClient", PaymentClient.class,
                () -> PaymentClientFactory.create(baseUrl, 3000));
        context.registerBean("payment", CircuitBreaker.class,
                () -> CircuitBreaker.of("payment", config));
        context.registerBean(OrderService.class);
        context.refresh();

        orderService = context.getBean(OrderService.class);
        circuitBreaker = context.getBean("payment", CircuitBreaker.class);
    }

    @AfterEach
    void tearDown() {
        context.close();
        server.stop();
    }

    @Test
    void breakerTripsOnRealFailuresThenRecoversAfterRealWait() throws Exception {
        System.out.println("--- Step 1: healthy downstream ---");
        OrderService.ChargeResult healthy = orderService.charge("order-1", 42.00);
        System.out.println("Result: " + healthy);
        assertTrue(healthy.success());
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());

        System.out.println("--- Step 2: downstream starts failing, drive 5 real failed calls ---");
        server.setMode(FlakyPaymentServer.Mode.FAILING);
        for (int i = 1; i <= 5; i++) {
            OrderService.ChargeResult result = orderService.charge("order-fail-" + i, 10.00);
            System.out.println("Call " + i + ": " + result + " | breaker state=" + circuitBreaker.getState());
        }
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
        int requestsBeforeOpen = server.requestCount();

        System.out.println("--- Step 3: breaker OPEN -- next call must fail fast, no network call ---");
        OrderService.ChargeResult rejected = orderService.charge("order-rejected", 10.00);
        System.out.println("Result: " + rejected);
        assertFalse(rejected.success());
        assertTrue(rejected.fallbackReason().startsWith("circuit-open"));
        assertEquals(requestsBeforeOpen, server.requestCount(),
                "a call while OPEN must never reach the real server");

        System.out.println("--- Step 4: wait out waitDurationInOpenState, downstream recovers ---");
        Thread.sleep(2200);
        server.setMode(FlakyPaymentServer.Mode.HEALTHY);

        OrderService.ChargeResult trial1 = orderService.charge("order-trial-1", 10.00);
        System.out.println("Trial call 1: " + trial1 + " | breaker state=" + circuitBreaker.getState());
        assertTrue(trial1.success());
        assertEquals(CircuitBreaker.State.HALF_OPEN, circuitBreaker.getState());

        OrderService.ChargeResult trial2 = orderService.charge("order-trial-2", 10.00);
        System.out.println("Trial call 2: " + trial2 + " | breaker state=" + circuitBreaker.getState());
        assertTrue(trial2.success());
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());

        System.out.println("--- Final state sequence confirmed: CLOSED -> OPEN -> HALF_OPEN -> CLOSED ---");
    }
}
