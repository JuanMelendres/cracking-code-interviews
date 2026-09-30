/**
 * Part 1: what a thread is, how you start one, and why the output order is not
 * yours to choose. Java 21, no dependencies.
 */
final class ThreadBasicsDemo {

    static void run() throws InterruptedException {
        System.out.println("== 1. Starting a thread: three ways, same result ==");
        System.out.println("  main thread is: " + Thread.currentThread().getName());

        // (a) Subclass Thread. Rarely the right choice -- you are inheriting
        // when you only need to pass behaviour.
        Thread a = new Thread() {
            @Override
            public void run() {
                System.out.println("  [a] subclassed Thread, running on: " + Thread.currentThread().getName());
            }
        };

        // (b) Implement Runnable and hand it to a Thread. Runnable is just
        // "some code to run" -- it has no thread of its own.
        Runnable job = () -> System.out.println("  [b] Runnable, running on: " + Thread.currentThread().getName());
        Thread b = new Thread(job, "worker-b");

        // (c) The same thing as a lambda passed inline.
        Thread c = new Thread(
                () -> System.out.println("  [c] lambda, running on: " + Thread.currentThread().getName()),
                "worker-c");

        a.start();
        b.start();
        c.start();

        // join() blocks the CALLING thread until that thread finishes. Without
        // it, main can reach the end before the workers have printed anything.
        a.join();
        b.join();
        c.join();
        System.out.println("  all three joined; main continues\n");

        System.out.println("== 2. start() versus run(): the difference that matters ==");
        Thread d = new Thread(() -> System.out.println("    ran on: " + Thread.currentThread().getName()));
        System.out.print("  calling d.run()   -> ");
        d.run();      // NOT a new thread: an ordinary method call on the current thread.
        System.out.print("  calling d.start() -> ");
        d.start();    // A real new thread.
        d.join();
        System.out.println();

        System.out.println("== 3. Interleaving: the output order is not yours to choose ==");
        // Starting threads one by one is not enough to show interleaving: each
        // trivial task finishes before the next thread is even created. A start
        // gate releases all of them at once, which is what a real workload does.
        java.util.concurrent.CountDownLatch gate = new java.util.concurrent.CountDownLatch(1);
        StringBuilder order = new StringBuilder();
        Thread[] workers = new Thread[6];
        for (int i = 0; i < workers.length; i++) {
            final int id = i;
            workers[i] = new Thread(() -> {
                try {
                    gate.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                // A small, varying amount of work, so finishing order is not
                // simply starting order.
                long sink = 0;
                for (int n = 0; n < 200_000 * (id % 3 + 1); n++) {
                    sink += n;
                }
                if (sink == Long.MIN_VALUE) {
                    System.out.print("");   // keep the loop from being optimised away
                }
                synchronized (order) {
                    order.append(id).append(' ');
                }
            }, "w" + i);
            workers[i].start();
        }
        gate.countDown();   // release all six simultaneously
        for (Thread t : workers) {
            t.join();
        }
        System.out.println("  started 0 1 2 3 4 5, finished in order: " + order.toString().trim());
        System.out.println("  Thread scheduling is the operating system's decision, not the");
        System.out.println("  program's. Code that depends on a particular finishing order is");
        System.out.println("  relying on something nobody guaranteed.\n");
    }
}
