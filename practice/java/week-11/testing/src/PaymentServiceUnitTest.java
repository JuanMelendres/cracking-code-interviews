import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * T-1103 -- a unit test with a mocked PaymentGateway (a test double,
 * specifically a mock: it doesn't hit a real network, and we assert
 * how many times it was called, not just what it returns). This is
 * the base of the test pyramid: fast, isolated, no real dependency.
 */
public class PaymentServiceUnitTest {

    @Test
    void succeedsOnThirdAttemptAfterTwoFailures() throws Exception {
        PaymentGateway gateway = mock(PaymentGateway.class);
        doThrow(new RuntimeException("network blip"))
                .doThrow(new RuntimeException("network blip"))
                .doNothing()
                .when(gateway).charge(anyString(), anyLong());

        PaymentService service = new PaymentService(gateway, 3);
        boolean result = service.processPayment("cust-1", 5000);

        assertTrue(result, "should succeed on the 3rd attempt");
        verify(gateway, times(3)).charge("cust-1", 5000);
    }

    @Test
    void exhaustsRetriesAndReturnsFalseOnPermanentFailure() throws Exception {
        PaymentGateway gateway = mock(PaymentGateway.class);
        doThrow(new RuntimeException("gateway down")).when(gateway).charge(anyString(), anyLong());

        PaymentService service = new PaymentService(gateway, 3);
        boolean result = service.processPayment("cust-2", 2500);

        assertFalse(result, "should give up after maxAttempts");
        verify(gateway, times(3)).charge("cust-2", 2500);
    }

    @Test
    void succeedsImmediatelyWithNoRetriesNeeded() throws Exception {
        PaymentGateway gateway = mock(PaymentGateway.class); // default: does nothing, no exception

        PaymentService service = new PaymentService(gateway, 3);
        boolean result = service.processPayment("cust-3", 1000);

        assertTrue(result);
        verify(gateway, times(1)).charge("cust-3", 1000); // exactly once -- no wasted retries
    }
}
