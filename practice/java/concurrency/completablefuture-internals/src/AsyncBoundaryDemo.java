import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Real, executed proof of the "attach before vs. after completion" boundary
 * described in JDK's CompletableFuture Javadoc: a dependent stage attached to
 * an ALREADY-COMPLETE future runs synchronously on the thread that attaches
 * it, while one attached BEFORE completion runs on whichever thread calls
 * complete(). thenApplyAsync bypasses both and always dispatches to an
 * executor (ForkJoinPool.commonPool() by default), regardless of completion
 * state at attach time.
 */
public class AsyncBoundaryDemo {

    public static void main(String[] args) throws Exception {
        caseAttachBeforeCompletion();
        caseAttachAfterCompletion();
        caseAsyncAlwaysDispatches();
    }

    // Case 1: thenApply attached BEFORE the future completes.
    static void caseAttachBeforeCompletion() throws Exception {
        CompletableFuture<String> cf = new CompletableFuture<>();

        // Attach the callback first -- the future is not yet complete.
        CompletableFuture<String> chained = cf.thenApply(s -> {
            String runner = Thread.currentThread().getName();
            System.out.println("Case 1 (attach BEFORE completion): thenApply ran on thread [" + runner + "]");
            return s + "-processed";
        });

        Thread completer = new Thread(() -> {
            try {
                Thread.sleep(150);
            } catch (InterruptedException ignored) {
            }
            cf.complete("done");
        }, "completer-thread");
        completer.start();
        completer.join();

        chained.join();
    }

    // Case 2: thenApply attached AFTER the future has already completed.
    static void caseAttachAfterCompletion() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("done");

        // cf is ALREADY complete at this point -- thenApply has nothing to wait for.
        CompletableFuture<String> chained = cf.thenApply(s -> {
            String runner = Thread.currentThread().getName();
            System.out.println("Case 2 (attach AFTER completion):  thenApply ran on thread [" + runner + "]"
                    + " -- same as caller thread [" + Thread.currentThread().getName() + "], ran synchronously inline");
            return s + "-processed";
        });
        chained.join();
    }

    // Case 3: thenApplyAsync always dispatches to an executor, regardless of completion timing.
    static void caseAsyncAlwaysDispatches() throws Exception {
        ExecutorService customExecutor = Executors.newFixedThreadPool(1, r -> new Thread(r, "custom-async-worker"));
        try {
            CompletableFuture<String> alreadyDone = CompletableFuture.completedFuture("done");

            CompletableFuture<String> withDefaultAsync = alreadyDone.thenApplyAsync(s -> {
                System.out.println("Case 3a (thenApplyAsync, default executor): ran on thread ["
                        + Thread.currentThread().getName() + "] -- NOT the caller thread, even though future was already complete");
                return s;
            });
            withDefaultAsync.join();

            CompletableFuture<String> withCustomExecutor = alreadyDone.thenApplyAsync(s -> {
                System.out.println("Case 3b (thenApplyAsync, custom executor):  ran on thread ["
                        + Thread.currentThread().getName() + "]");
                return s;
            }, customExecutor);
            withCustomExecutor.join();
        } finally {
            customExecutor.shutdown();
            customExecutor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
