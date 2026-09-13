import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Real, executed proof that an exception thrown inside a CompletableFuture
 * pipeline is silently swallowed unless something forces the result --
 * join(), get(), exceptionally(), or handle(). "Fire and forget" on a
 * CompletableFuture pipeline is a real, silent failure mode: no stack trace
 * is ever printed and the caller has no idea the task failed.
 */
public class ExceptionSwallowingDemo {

    public static void main(String[] args) throws Exception {
        fireAndForgetSwallowsTheException();
        joinSurfacesTheRealException();
        handleRecoversWithoutThrowing();
    }

    // A pipeline that throws, with NOTHING ever calling join()/get() on it.
    static void fireAndForgetSwallowsTheException() throws InterruptedException {
        System.out.println("== Fire-and-forget: exception is thrown but NEVER observed ==");
        CompletableFuture.supplyAsync(() -> {
            if (true) {
                throw new IllegalStateException("simulated downstream failure");
            }
            return "unreachable";
        }).thenApply(s -> s + "-processed");
        // Deliberately never call join()/get()/exceptionally()/handle() on the chain above.

        Thread.sleep(200); // give the async task time to actually run and throw
        System.out.println("Main thread reached this line normally -- no exception, no stack trace, no log line."
                + " The failure happened on a background thread and vanished.");
    }

    // The same pipeline, but this time join() is called -- the real exception surfaces.
    static void joinSurfacesTheRealException() {
        System.out.println("\n== Same pipeline, but join() is called -- exception surfaces for real ==");
        CompletableFuture<String> pipeline = CompletableFuture.supplyAsync(() -> {
            throw new IllegalStateException("simulated downstream failure");
        }).thenApply(s -> s + "-processed");

        try {
            pipeline.join();
            System.out.println("unreachable -- join() should have thrown");
        } catch (CompletionException e) {
            System.out.println("join() threw CompletionException, real cause: "
                    + e.getCause().getClass().getName() + ": " + e.getCause().getMessage());
        }
    }

    // handle() observes both outcomes and can recover instead of propagating.
    static void handleRecoversWithoutThrowing() {
        System.out.println("\n== handle() observes the failure and recovers, without throwing ==");
        CompletableFuture<String> failing = CompletableFuture.<String>supplyAsync(() -> {
            throw new IllegalStateException("simulated downstream failure");
        });
        CompletableFuture<String> recovered = failing.handle((result, ex) -> {
            if (ex != null) {
                System.out.println("handle() saw the real exception: " + ex.getCause().getClass().getSimpleName());
                return "fallback-value";
            }
            return result;
        });

        String value = recovered.join();
        System.out.println("Final value after recovery: " + value);
    }
}
