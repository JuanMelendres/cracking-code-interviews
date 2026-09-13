public class SuppressedExceptionDemo {

    static class FlakyResource implements AutoCloseable {
        private final String name;
        FlakyResource(String name) { this.name = name; }

        void doWork() {
            throw new RuntimeException(name + ": failure during doWork()");
        }

        @Override
        public void close() {
            // close() ALSO throws -- this is the realistic case (e.g. a
            // network socket failing to flush during close after the
            // primary operation already failed).
            throw new RuntimeException(name + ": failure during close()");
        }
    }

    public static void main(String[] args) {
        System.out.println("== try-with-resources: what happens when BOTH the body and close() throw ==");
        try {
            try (FlakyResource r = new FlakyResource("resource-A")) {
                r.doWork();
            }
        } catch (RuntimeException e) {
            System.out.println("Primary exception propagated: " + e.getMessage());
            System.out.println("(this is the doWork() failure -- try-with-resources always propagates");
            System.out.println(" the exception from the BODY as primary, not the one from close())");
            Throwable[] suppressed = e.getSuppressed();
            System.out.println("e.getSuppressed().length = " + suppressed.length);
            for (Throwable s : suppressed) {
                System.out.println("  suppressed: " + s.getMessage()
                        + "  (the close() failure -- NOT lost, but demoted to suppressed so it's still visible)");
            }
        }

        System.out.println();
        System.out.println("== Without try-with-resources, a manual finally block LOSES the original exception ==");
        FlakyResource manual = new FlakyResource("resource-B");
        try {
            try {
                manual.doWork();
            } finally {
                manual.close(); // this throw REPLACES the doWork() exception entirely
            }
        } catch (RuntimeException e) {
            System.out.println("Exception that actually propagated: " + e.getMessage());
            System.out.println("(the ORIGINAL doWork() failure is completely gone -- a manual finally-block");
            System.out.println(" close() that also throws silently replaces it, with no suppressed-exception");
            System.out.println(" mechanism to recover it. This is exactly why try-with-resources exists.)");
        }
    }
}
