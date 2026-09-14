import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Real, measured proof that work-stealing actually happens: a deliberately
 * UNBALANCED task tree (one branch has far more leaf tasks than its
 * siblings) is submitted to a small, fixed-size pool. The thread that
 * happens to grab the "light" branch finishes fast and, instead of sitting
 * idle, steals leaf tasks off a BUSY sibling worker's own queue --
 * ForkJoinPool.getStealCount() is a real, public JDK metric that proves
 * this happened, not an inferred assumption.
 */
public class WorkStealingProofDemo {

    static final AtomicInteger leavesExecuted = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        runOnPool(4, "4-worker pool (stealing possible)");
        leavesExecuted.set(0);
        runOnPool(1, "1-worker pool (NO other worker to steal from -- real control)");
    }

    static void runOnPool(int parallelism, String label) throws InterruptedException {
        ForkJoinPool pool = new ForkJoinPool(parallelism);

        // Deliberately unbalanced: one top-level branch spawns 4,000 cheap
        // leaf tasks; a sibling branch spawns only 4. Whichever worker
        // draws the light branch will finish almost immediately and, under
        // a genuinely single-worker-queue design, would sit idle for the
        // rest -- work-stealing is what prevents that.
        UnbalancedTask heavy = new UnbalancedTask(4000);
        UnbalancedTask light = new UnbalancedTask(4);

        long start = System.currentTimeMillis();
        pool.invoke(new RootTask(heavy, light));
        long elapsed = System.currentTimeMillis() - start;

        System.out.println("\n== " + label + " ==");
        System.out.println("Total leaf tasks executed: " + leavesExecuted.get() + " (expected 4004)");
        System.out.println("Real elapsed: " + elapsed + "ms");
        System.out.println("Real ForkJoinPool.getStealCount(): " + pool.getStealCount());

        pool.shutdown();
    }

    static class RootTask extends RecursiveAction {
        final UnbalancedTask heavy, light;

        RootTask(UnbalancedTask heavy, UnbalancedTask light) {
            this.heavy = heavy;
            this.light = light;
        }

        @Override
        protected void compute() {
            heavy.fork();
            light.compute();
            heavy.join();
        }
    }

    static class UnbalancedTask extends RecursiveAction {
        final int leafCount;

        UnbalancedTask(int leafCount) {
            this.leafCount = leafCount;
        }

        @Override
        protected void compute() {
            if (leafCount <= 1) {
                // A tiny, real unit of work -- enough that stealing it has a
                // real, measurable cost/benefit, not an instant no-op.
                long x = 0;
                for (int i = 0; i < 50_000; i++) x += i * i;
                leavesExecuted.incrementAndGet();
                return;
            }
            int half = leafCount / 2;
            UnbalancedTask left = new UnbalancedTask(half);
            UnbalancedTask right = new UnbalancedTask(leafCount - half);
            left.fork();
            right.compute();
            left.join();
        }
    }
}
