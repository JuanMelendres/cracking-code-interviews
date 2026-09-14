import java.io.IOException;

public class SwallowedCauseDemo {

    static class OrderProcessingException extends RuntimeException {
        // BROKEN: only takes a message, no cause -- the original exception
        // (and its stack trace) is thrown away.
        OrderProcessingException(String message) { super(message); }
    }

    static class OrderProcessingExceptionFixed extends RuntimeException {
        // FIXED: chains the cause, per Throwable(String, Throwable).
        OrderProcessingExceptionFixed(String message, Throwable cause) { super(message, cause); }
    }

    static void lowLevelIO() throws IOException {
        throw new IOException("disk full on volume /data");
    }

    static void brokenWrapper() {
        try {
            lowLevelIO();
        } catch (IOException e) {
            // ANTI-PATTERN: rethrows a new exception without the cause.
            throw new OrderProcessingException("could not process order");
        }
    }

    static void fixedWrapper() {
        try {
            lowLevelIO();
        } catch (IOException e) {
            throw new OrderProcessingExceptionFixed("could not process order", e);
        }
    }

    public static void main(String[] args) {
        System.out.println("== Wrapping without chaining the cause: the real root cause is gone ==");
        try {
            brokenWrapper();
        } catch (OrderProcessingException e) {
            System.out.println("Caught: " + e);
            System.out.println("e.getCause() = " + e.getCause() + "  (null -- the IOException and its stack trace are LOST)");
        }

        System.out.println();
        System.out.println("== Wrapping WITH the cause chained: root cause is preserved ==");
        try {
            fixedWrapper();
        } catch (OrderProcessingExceptionFixed e) {
            System.out.println("Caught: " + e);
            System.out.println("e.getCause() = " + e.getCause() + "  (the real IOException, recoverable for logging/debugging)");
            System.out.println();
            System.out.println("Full chained stack trace (printStackTrace):");
            e.printStackTrace(System.out);
        }
    }
}
