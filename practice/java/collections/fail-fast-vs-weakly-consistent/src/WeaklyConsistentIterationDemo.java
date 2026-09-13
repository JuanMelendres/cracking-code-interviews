import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Real, executed proof that CopyOnWriteArrayList and ConcurrentHashMap
 * iterators are weakly consistent: they NEVER throw
 * ConcurrentModificationException under real concurrent modification from
 * another thread, unlike ArrayList's fail-fast (and undefined-if-truly-
 * concurrent) iterator.
 */
public class WeaklyConsistentIterationDemo {

    public static void main(String[] args) throws InterruptedException {
        copyOnWriteArrayListSeesAFixedSnapshot();
        concurrentHashMapNeverThrowsDuringConcurrentMutation();
    }

    // The COW iterator is built over the array snapshot captured at
    // iterator-creation time -- later writes replace the underlying array
    // reference entirely, but the already-created iterator keeps its own
    // reference to the OLD array. It genuinely cannot see the mutation.
    static void copyOnWriteArrayListSeesAFixedSnapshot() throws InterruptedException {
        System.out.println("== CopyOnWriteArrayList: iterator holds a fixed snapshot, real concurrent add() is invisible to it ==");
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>(List.of("a", "b", "c"));

        var iterator = list.iterator(); // snapshot captured HERE, before the concurrent add below

        Thread writer = new Thread(() -> list.add("d-added-after-snapshot"));
        writer.start();
        writer.join(); // real thread, real completion, guaranteed to have run before we iterate

        System.out.println("Live list after concurrent add: " + list);
        StringBuilder seen = new StringBuilder();
        while (iterator.hasNext()) {
            seen.append(iterator.next()).append(" ");
        }
        System.out.println("Elements seen by the already-created iterator: " + seen.toString().trim()
                + "  -- no exception thrown, and the concurrently-added element is genuinely absent from this iteration");
    }

    // CountDownLatch-controlled, not timing-guessed: the reader thread
    // deterministically PAUSES mid-iteration (after its first entry) and
    // waits for the writer to genuinely finish inserting before resuming --
    // guaranteeing real overlap between live iteration and concurrent
    // mutation, instead of hoping the OS schedules both threads to overlap.
    static void concurrentHashMapNeverThrowsDuringConcurrentMutation() throws InterruptedException {
        System.out.println("\n== ConcurrentHashMap: real, latch-forced concurrent put() DURING live iteration, zero exceptions ==");
        Map<Integer, String> map = new ConcurrentHashMap<>();
        for (int i = 0; i < 5; i++) map.put(i, "v" + i);

        CountDownLatch readerReachedFirstEntry = new CountDownLatch(1);
        CountDownLatch writerFinishedInserting = new CountDownLatch(1);

        int[] iterationCount = {0};
        boolean[] threw = {false};
        boolean[] sawAConcurrentlyInsertedKey = {false};

        Thread reader = new Thread(() -> {
            try {
                boolean first = true;
                for (Map.Entry<Integer, String> entry : map.entrySet()) {
                    iterationCount[0]++;
                    if (entry.getKey() >= 1000) sawAConcurrentlyInsertedKey[0] = true;
                    if (first) {
                        first = false;
                        readerReachedFirstEntry.countDown();
                        writerFinishedInserting.await(); // genuinely pause mid-iteration
                    }
                }
            } catch (java.util.ConcurrentModificationException e) {
                threw[0] = true;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread writer = new Thread(() -> {
            try {
                readerReachedFirstEntry.await(); // wait until the reader is genuinely mid-iteration
                for (int i = 1000; i < 11000; i++) {
                    map.put(i, "v" + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                writerFinishedInserting.countDown();
            }
        });

        reader.start();
        writer.start();
        reader.join();
        writer.join();

        System.out.println("Iterated " + iterationCount[0] + " entries; the writer inserted 10,000 more keys"
                + " WHILE this iteration was genuinely paused mid-traversal (latch-forced, not timing-guessed)."
                + " CME thrown: " + threw[0] + ". Saw at least one of the concurrently-inserted keys during the"
                + " same iteration: " + sawAConcurrentlyInsertedKey[0]
                + " -- weakly-consistent: never throws; MAY or may not reflect an in-flight concurrent insert,"
                + " unlike CopyOnWriteArrayList's fixed snapshot above. Final map size=" + map.size());
    }
}
