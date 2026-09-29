import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Mockito 5.11.0, JUnit 5.10.2, OpenJDK 21. Every assertion below is a measured
 * claim about how @Mock and @Spy actually behave, not a restatement of the
 * documentation.
 */
class MockVsSpyTest {

    @Test
    @DisplayName("mock: real code never runs and unstubbed methods return type defaults")
    void mockReturnsDefaultsAndRunsNothing() {
        AuditLog log = mock(AuditLog.class);

        String result = log.write("hello");

        assertNull(result, "an unstubbed mock returns the type default, here null for String");
        assertEquals(0, log.realCallCount(), "realCallCount is itself mocked, so it returns 0 regardless");
        System.out.println("[mock]  write(\"hello\") returned: " + result);
        System.out.println("[mock]  realCallCount() returned: " + log.realCallCount()
                + "   <- also a mocked method, NOT the real field");
    }

    @Test
    @DisplayName("spy: real code runs by default")
    void spyRunsRealCodeByDefault() {
        AuditLog log = spy(new AuditLog());

        String result = log.write("hello");

        assertEquals("WROTE:hello", result);
        assertEquals(1, log.realCallCount());
        assertTrue(log.written().contains("hello"));
        System.out.println("[spy]   write(\"hello\") returned: " + result);
        System.out.println("[spy]   realCallCount() returned: " + log.realCallCount()
                + "   <- the real method really ran");
    }

    @Test
    @DisplayName("TRAP: when(spy.x()) executes the real method while stubbing it")
    void whenOnSpyExecutesTheRealMethod() {
        AuditLog log = spy(new AuditLog());

        assertEquals(0, log.realCallCount(), "nothing has run yet");

        // This line is the trap. Mockito has to evaluate the argument to when(),
        // which means calling write("hello") on the spy -- for real.
        when(log.write("hello")).thenReturn("STUBBED");

        System.out.println("[spy]   after when(log.write(\"hello\")).thenReturn(...):");
        System.out.println("[spy]     realCallCount() = " + log.realCallCount()
                + "   <- the real method ALREADY RAN during stubbing");
        System.out.println("[spy]     written()       = " + log.written()
                + "   <- and its side effect is still there");

        assertEquals(1, log.realCallCount(), "the real write() ran once, during stubbing");
        assertTrue(log.written().contains("hello"), "its side effect leaked into real state");

        assertEquals("STUBBED", log.write("hello"), "the stub does apply from now on");
        assertEquals(1, log.realCallCount(), "and the stubbed call does not run the real method");
    }

    @Test
    @DisplayName("FIX: doReturn().when(spy) never executes the real method")
    void doReturnOnSpyDoesNotExecuteTheRealMethod() {
        AuditLog log = spy(new AuditLog());

        doReturn("STUBBED").when(log).write("hello");

        System.out.println("[spy]   after doReturn(\"STUBBED\").when(log).write(\"hello\"):");
        System.out.println("[spy]     realCallCount() = " + log.realCallCount()
                + "   <- real method never ran");
        System.out.println("[spy]     written()       = " + log.written());

        assertEquals(0, log.realCallCount(), "doReturn() stubs without invoking the real method");
        assertTrue(log.written().isEmpty(), "no side effect leaked");

        assertEquals("STUBBED", log.write("hello"));
        assertEquals(0, log.realCallCount());
    }

    @Test
    @DisplayName("self-invocation: does a stub apply when a real method calls it internally?")
    void selfInvocationInsideARealMethod() {
        AuditLog log = spy(new AuditLog());

        doReturn("STUBBED").when(log).write("x");

        String result = log.writeTwice("x");

        System.out.println("[spy]   writeTwice(\"x\") returned: " + result);
        System.out.println("[spy]     realCallCount() = " + log.realCallCount());
        System.out.println("[spy]     written()       = " + log.written());

        // Recorded, not predicted: the assertion below is whatever the run
        // actually produced. See the README for what it turned out to be.
        assertEquals("STUBBED|STUBBED", result,
                "internal this.write() calls go through the spy proxy, so the stub applies");
        assertEquals(0, log.realCallCount());
    }

    @Test
    @DisplayName("can a final method be stubbed on a Mockito 5 spy?")
    void finalMethodOnASpy() {
        AuditLog log = spy(new AuditLog());

        doReturn("STUBBED").when(log).sealedWrite("x");
        String result = log.sealedWrite("x");

        System.out.println("[spy]   sealedWrite(\"x\") (a final method) returned: " + result);
        System.out.println("[spy]     realCallCount() = " + log.realCallCount());

        // Recorded, not predicted. Mockito 5 ships the inline mock maker as its
        // default, which instruments final methods via a Java agent rather than
        // by subclassing -- so the pre-Mockito-5 rule does not hold here.
        assertEquals("STUBBED", result, "the inline mock maker can stub a final method");
        assertEquals(0, log.realCallCount());
    }

    @Test
    @DisplayName("verify() works identically on a mock and on a spy")
    void verifyWorksOnBoth() {
        AuditLog mocked = mock(AuditLog.class);
        AuditLog spied = spy(new AuditLog());

        mocked.write("a");
        mocked.write("a");
        spied.write("a");
        spied.write("a");

        verify(mocked, times(2)).write("a");
        verify(spied, times(2)).write("a");
        System.out.println("[both]  verify(times(2)).write(\"a\") passed on mock AND spy");
    }
}
