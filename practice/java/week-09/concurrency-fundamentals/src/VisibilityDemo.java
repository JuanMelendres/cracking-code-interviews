public class VisibilityDemo {
    static boolean stopPlain = false;
    static volatile boolean stopVolatile = false;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("== non-volatile flag: does the worker thread ever see the update? ==");
        runTrial(false);

        System.out.println();
        System.out.println("== volatile flag: same test ==");
        runTrial(true);
    }

    static void runTrial(boolean useVolatile) throws InterruptedException {
        long[] iterations = new long[1];
        Thread worker = new Thread(() -> {
            long i = 0;
            if (useVolatile) {
                while (!stopVolatile) { i++; }
            } else {
                while (!stopPlain) { i++; }
            }
            iterations[0] = i;
        });
        worker.setDaemon(true);
        worker.start();
        Thread.sleep(1500); // let the loop run long enough for the JIT to compile and optimize it
        long setAt = System.nanoTime();
        if (useVolatile) stopVolatile = true; else stopPlain = true;

        worker.join(5000);
        long waited = (System.nanoTime() - setAt) / 1_000_000;
        if (worker.isAlive()) {
            System.out.printf("worker STILL RUNNING %dms after the flag was set -- update never observed (this run)%n", waited);
        } else {
            System.out.printf("worker stopped %dms after the flag was set, having run %d iterations%n", waited, iterations[0]);
        }
    }
}
