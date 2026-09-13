import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // LC 1117: Building H2O — verify every consecutive pair of releases is exactly 2 H then 1 O
        System.out.println("== LC 1117: Building H2O (10 molecules, verify strict HHO grouping) ==");
        {
            int molecules = 10;
            List<String> log = new CopyOnWriteArrayList<>();
            H2O h2o = new H2O();
            Thread[] hThreads = new Thread[2 * molecules];
            Thread[] oThreads = new Thread[molecules];
            for (int i = 0; i < 2 * molecules; i++) {
                hThreads[i] = new Thread(() -> run(() -> h2o.hydrogen(() -> log.add("H"))));
            }
            for (int i = 0; i < molecules; i++) {
                oThreads[i] = new Thread(() -> run(() -> h2o.oxygen(() -> log.add("O"))));
            }
            for (Thread t : hThreads) t.start();
            for (Thread t : oThreads) t.start();
            for (Thread t : hThreads) t.join();
            for (Thread t : oThreads) t.join();

            Check.eq(3 * molecules, log.size(), "H2O total releases = " + (3 * molecules) + " (2H+1O per molecule)");
            long hCount = log.stream().filter(s -> s.equals("H")).count();
            long oCount = log.stream().filter(s -> s.equals("O")).count();
            Check.eq((long) 2 * molecules, hCount, "H2O exactly " + (2 * molecules) + " H releases");
            Check.eq((long) molecules, oCount, "H2O exactly " + molecules + " O releases");
            // verify no more than 2 H ever appear before an O (bond ratio never exceeds 2:1 in progress)
            int hSinceLastO = 0;
            boolean ratioHeld = true;
            for (String s : log) {
                if (s.equals("H")) hSinceLastO++;
                else { if (hSinceLastO != 2) ratioHeld = false; hSinceLastO = 0; }
            }
            Check.isTrue(ratioHeld, "H2O every O is preceded by exactly 2 H since the prior O (real bonding order)");
        }

        // LC 1195: Fizz Buzz Multithreaded — verify exact output sequence for n=20
        System.out.println("\n== LC 1195: Fizz Buzz Multithreaded (n=20, verify exact sequence) ==");
        {
            int n = 20;
            List<String> log = new CopyOnWriteArrayList<>();
            FizzBuzzMultithreaded fb = new FizzBuzzMultithreaded(n);
            Thread fizzT = new Thread(() -> run(() -> fb.fizz(() -> log.add("fizz"))));
            Thread buzzT = new Thread(() -> run(() -> fb.buzz(() -> log.add("buzz"))));
            Thread fbT = new Thread(() -> run(() -> fb.fizzbuzz(() -> log.add("fizzbuzz"))));
            Thread numT = new Thread(() -> run(() -> fb.number(v -> log.add(String.valueOf(v)))));
            fizzT.start(); buzzT.start(); fbT.start(); numT.start();
            fizzT.join(); buzzT.join(); fbT.join(); numT.join();

            StringBuilder expected = new StringBuilder();
            for (int i = 1; i <= n; i++) {
                if (i % 15 == 0) expected.append("fizzbuzz");
                else if (i % 3 == 0) expected.append("fizz");
                else if (i % 5 == 0) expected.append("buzz");
                else expected.append(i);
                expected.append(",");
            }
            String actual = String.join(",", log) + ",";
            Check.eq(expected.toString(), actual, "fizzbuzz(20) exact sequence matches single-threaded reference");
        }

        // LC 1226: Dining Philosophers — verify no deadlock (all threads finish) and correct eat counts
        System.out.println("\n== LC 1226: Dining Philosophers (5 philosophers x 50 meals, verify no deadlock) ==");
        {
            int meals = 50;
            DiningPhilosophers dp = new DiningPhilosophers();
            AtomicInteger[] eatCounts = new AtomicInteger[5];
            for (int i = 0; i < 5; i++) eatCounts[i] = new AtomicInteger(0);
            Thread[] philosophers = new Thread[5];
            for (int p = 0; p < 5; p++) {
                final int id = p;
                philosophers[p] = new Thread(() -> {
                    for (int m = 0; m < meals; m++) {
                        run(() -> dp.wantsToEat(id, () -> {}, () -> {}, eatCounts[id]::incrementAndGet, () -> {}, () -> {}));
                    }
                });
            }
            long start = System.currentTimeMillis();
            for (Thread t : philosophers) t.start();
            for (Thread t : philosophers) t.join(10_000); // bounded join: a real deadlock would time out here
            long elapsed = System.currentTimeMillis() - start;
            Check.isTrue(elapsed < 10_000, "all 5 philosophers finished without deadlocking (took " + elapsed + "ms)");
            boolean allAte = true;
            for (int i = 0; i < 5; i++) if (eatCounts[i].get() != meals) allAte = false;
            Check.isTrue(allAte, "every philosopher ate exactly " + meals + " times, none starved");
        }

        // LC 1188: Design Bounded Blocking Queue — real producer/consumer, verify capacity never exceeded and all items delivered
        System.out.println("\n== LC 1188: Design Bounded Blocking Queue (capacity=5, 200 items, real producer/consumer) ==");
        {
            int capacity = 5;
            int itemCount = 200;
            BoundedBlockingQueue queue = new BoundedBlockingQueue(capacity);
            AtomicInteger maxObservedSize = new AtomicInteger(0);
            List<Integer> consumed = new CopyOnWriteArrayList<>();

            Thread producer = new Thread(() -> run(() -> {
                for (int i = 1; i <= itemCount; i++) {
                    queue.enqueue(i);
                    maxObservedSize.updateAndGet(prev -> Math.max(prev, queue.size()));
                }
            }));
            Thread consumer = new Thread(() -> run(() -> {
                for (int i = 0; i < itemCount; i++) {
                    consumed.add(queue.dequeue());
                }
            }));
            producer.start();
            consumer.start();
            producer.join();
            consumer.join();

            Check.eq(itemCount, consumed.size(), "bounded queue delivered all " + itemCount + " items");
            boolean strictOrder = true;
            for (int i = 0; i < itemCount; i++) if (consumed.get(i) != i + 1) strictOrder = false;
            Check.isTrue(strictOrder, "bounded queue preserved FIFO order under real concurrent producer/consumer");
            Check.isTrue(maxObservedSize.get() <= capacity, "queue size never observed above capacity=" + capacity + " (was " + maxObservedSize.get() + ")");
        }

        Check.summary("Week 22 — Concurrency Coding (LC 1117, 1195, 1226, 1188)");
        if (Check.fail > 0) System.exit(1);
    }

    interface ThrowingRunnable {
        void run() throws InterruptedException;
    }

    static void run(ThrowingRunnable r) {
        try { r.run(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
