/**
 * Part 3: the problem volatile DOES solve. A reader thread spins until a flag
 * flips. Without volatile the write may never become visible to it.
 *
 * <p>This is deliberately reported as whatever actually happens on this JVM and
 * machine, including if the non-volatile case terminates normally -- visibility
 * is a guarantee the language withholds, not a failure it promises.
 */
final class VisibilityDemo {

    static boolean plainFlag = false;
    static volatile boolean volatileFlag = false;

    static void run() throws InterruptedException {
        System.out.println("== 5. Visibility: the problem volatile actually solves ==");

        // Non-volatile flag, bounded so the demo cannot hang forever.
        plainFlag = false;
        long[] plainSpins = new long[1];
        Thread plainReader = new Thread(() -> {
            long spins = 0;
            long deadline = System.nanoTime() + 3_000_000_000L;   // 3s cap
            while (!plainFlag && System.nanoTime() < deadline) {
                spins++;
            }
            plainSpins[0] = spins;
        }, "plain-reader");
        plainReader.start();
        Thread.sleep(200);
        plainFlag = true;
        long start = System.nanoTime();
        plainReader.join();
        long plainMs = (System.nanoTime() - start) / 1_000_000;

        // volatile flag.
        volatileFlag = false;
        Thread volatileReader = new Thread(() -> {
            while (!volatileFlag) {
                // spin
            }
        }, "volatile-reader");
        volatileReader.start();
        Thread.sleep(200);
        volatileFlag = true;
        start = System.nanoTime();
        volatileReader.join();
        long volatileMs = (System.nanoTime() - start) / 1_000_000;

        System.out.printf("  plain boolean flag : reader exited %,d ms after the write%s%n",
                plainMs, plainMs >= 2500 ? "  <- NEVER SAW IT (hit the 3s cap)" : "");
        System.out.printf("  volatile flag      : reader exited %,d ms after the write%n", volatileMs);
        System.out.println("  (spins recorded by the plain reader: " + String.format("%,d", plainSpins[0]) + ")");
        System.out.println();
    }
}
