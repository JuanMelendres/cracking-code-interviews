import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("== LC 1114: Print in Order (100 randomized-scheduling trials) ==");
        boolean allInOrder = true;
        for (int trial = 0; trial < 100; trial++) {
            StringBuilder sb = new StringBuilder();
            PrintInOrder p = new PrintInOrder();
            Thread t1 = new Thread(() -> run(() -> p.first(() -> sb.append("1"))));
            Thread t2 = new Thread(() -> run(() -> p.second(() -> sb.append("2"))));
            Thread t3 = new Thread(() -> run(() -> p.third(() -> sb.append("3"))));
            // start in a DELIBERATELY wrong order (3, 1, 2) -- the class must
            // still enforce first/second/third regardless of start order
            t3.start(); t1.start(); t2.start();
            t1.join(); t2.join(); t3.join();
            if (!sb.toString().equals("123")) allInOrder = false;
        }
        Check.isTrue(allInOrder, "all 100 trials printed \"123\" regardless of thread start order (3,1,2)");

        System.out.println("\n== LC 1115: Print FooBar Alternately (n=1000, verify no foo-foo or bar-bar) ==");
        int n = 1000;
        FooBar fb = new FooBar(n);
        ConcurrentLinkedQueue<String> output = new ConcurrentLinkedQueue<>();
        Thread fooThread = new Thread(() -> run(() -> fb.foo(() -> output.add("foo"))));
        Thread barThread = new Thread(() -> run(() -> fb.bar(() -> output.add("bar"))));
        fooThread.start(); barThread.start();
        fooThread.join(); barThread.join();
        String joined = String.join(",", output);
        Check.eq(2 * n, output.size(), "foobar output has exactly " + (2 * n) + " entries");
        boolean alternates = true;
        String[] arr = joined.split(",");
        for (int i = 0; i < arr.length; i++) {
            if (!arr[i].equals(i % 2 == 0 ? "foo" : "bar")) { alternates = false; break; }
        }
        Check.isTrue(alternates, "output strictly alternates foo,bar,foo,bar,... for all " + (2 * n) + " entries");

        System.out.println("\n== LC 1116: Print Zero Even Odd (n=1000, verify 0,1,0,2,0,3,... pattern) ==");
        int m = 1000;
        ZeroEvenOdd zeo = new ZeroEvenOdd(m);
        ConcurrentLinkedQueue<Integer> zeoOutput = new ConcurrentLinkedQueue<>();
        Thread zt = new Thread(() -> run(() -> zeo.zero(zeoOutput::add)));
        Thread et = new Thread(() -> run(() -> zeo.even(zeoOutput::add)));
        Thread ot = new Thread(() -> run(() -> zeo.odd(zeoOutput::add)));
        zt.start(); et.start(); ot.start();
        zt.join(); et.join(); ot.join();
        Check.eq(2 * m, zeoOutput.size(), "zero-even-odd output has exactly " + (2 * m) + " entries");
        Integer[] zeoArr = zeoOutput.toArray(new Integer[0]);
        boolean correctPattern = true;
        int expectedNum = 1;
        for (int i = 0; i < zeoArr.length; i += 2) {
            if (zeoArr[i] != 0 || zeoArr[i + 1] != expectedNum) { correctPattern = false; break; }
            expectedNum++;
        }
        Check.isTrue(correctPattern, "output is exactly 0,1,0,2,0,3,...,0," + m);

        Check.summary("Week 9 concurrency coding suite");
        if (Check.fail > 0) System.exit(1);
    }

    interface ThrowingRunnable {
        void run() throws InterruptedException;
    }

    static void run(ThrowingRunnable r) {
        try { r.run(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
