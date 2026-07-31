import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ArrayBlockingQueue;

public class CollectionsCodingPractice {

    // Problem 1: a thread-safe frequency counter using ConcurrentHashMap.merge()
    // -- the atomic-increment discipline from T-205, applied.
    static Map<String, Integer> wordFrequency(List<String> words) throws InterruptedException {
        ConcurrentHashMap<String, Integer> freq = new ConcurrentHashMap<>();
        int threads = 4;
        List<Thread> workers = new ArrayList<>();
        int chunkSize = (words.size() + threads - 1) / threads;
        for (int t = 0; t < threads; t++) {
            int start = t * chunkSize;
            int end = Math.min(start + chunkSize, words.size());
            if (start >= end) continue;
            Thread worker = new Thread(() -> {
                for (int i = start; i < end; i++) {
                    freq.merge(words.get(i), 1, Integer::sum); // atomic per key
                }
            });
            workers.add(worker);
            worker.start();
        }
        for (Thread w : workers) w.join();
        return freq;
    }

    // Problem 2: choose the right structure -- a bounded producer/consumer
    // buffer using ArrayBlockingQueue (T-207), correctly draining under a
    // known total.
    static List<Integer> boundedProduceConsume(int itemCount, int capacity) throws InterruptedException {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(capacity);
        List<Integer> consumed = Collections.synchronizedList(new ArrayList<>());

        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < itemCount; i++) queue.put(i);
            } catch (InterruptedException ignored) {
            }
        });
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < itemCount; i++) consumed.add(queue.take());
            } catch (InterruptedException ignored) {
            }
        });
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        return consumed;
    }

    // Problem 3: a HashSet built on a correctly-designed value class
    // (connects back to Week 13's equals/hashCode discipline, applied to a
    // real HashMap-key use case).
    record Point(int x, int y) {}

    static int countDistinctPoints(List<Point> points) {
        return new HashSet<>(points).size(); // records auto-generate correct equals()/hashCode()
    }

    static int assertions = 0;
    static void check(boolean condition, String description) {
        assertions++;
        if (!condition) throw new AssertionError("FAILED: " + description);
        System.out.println("PASS: " + description);
    }

    public static void main(String[] args) throws InterruptedException {
        // Problem 1 checks
        List<String> words = new ArrayList<>();
        for (int i = 0; i < 10_000; i++) words.add("word" + (i % 5));
        Map<String, Integer> freq = wordFrequency(words);
        check(freq.size() == 5, "wordFrequency: exactly 5 distinct words");
        check(freq.values().stream().mapToInt(Integer::intValue).sum() == 10_000,
                "wordFrequency: total count across all words equals input size (no lost updates)");
        check(freq.get("word0") == 2000, "wordFrequency: word0 counted exactly 2000 times");

        // Problem 2 checks
        List<Integer> consumed = boundedProduceConsume(5000, 16);
        check(consumed.size() == 5000, "boundedProduceConsume: all 5000 items consumed, none lost");
        List<Integer> sorted = new ArrayList<>(consumed);
        Collections.sort(sorted);
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 5000; i++) expected.add(i);
        check(sorted.equals(expected), "boundedProduceConsume: every produced value 0..4999 consumed exactly once");

        // Problem 3 checks
        List<Point> points = List.of(new Point(1, 1), new Point(1, 1), new Point(2, 2), new Point(3, 3));
        check(countDistinctPoints(points) == 3, "countDistinctPoints: record equals()/hashCode() correctly dedupes");

        System.out.println();
        System.out.println(assertions + "/" + assertions + " assertions passed.");
    }
}
