import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("== ArrayBlockingQueue: put() blocks when full, until a consumer makes room ==");
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(2);
        queue.put(1);
        queue.put(2);
        System.out.println("Queue filled to capacity (2). Producer thread will now try to put a 3rd item...");

        long[] producerBlockedNanos = new long[1];
        Thread producer = new Thread(() -> {
            long start = System.nanoTime();
            try {
                queue.put(3); // BLOCKS here until the consumer below takes an item
            } catch (InterruptedException ignored) {
            }
            producerBlockedNanos[0] = System.nanoTime() - start;
        });
        producer.start();

        Thread.sleep(300); // let the producer genuinely block for a measurable, real duration
        System.out.println("300ms later, producer thread state: " + producer.getState()
                + "  (WAITING/BLOCKED -- confirms put() actually blocked, not returned immediately)");

        Integer consumed = queue.take(); // makes room -- unblocks the producer
        producer.join();
        System.out.printf("Consumer took %d, freeing a slot. Producer's put(3) was blocked for ~%d ms before returning.%n",
                consumed, producerBlockedNanos[0] / 1_000_000);
        System.out.println("Final queue contents: " + queue);

        System.out.println();
        System.out.println("== SynchronousQueue: capacity ZERO -- put() blocks until a consumer is ALREADY waiting ==");
        SynchronousQueue<String> handoff = new SynchronousQueue<>();
        long[] putNanos = new long[1];
        Thread handoffProducer = new Thread(() -> {
            long start = System.nanoTime();
            try {
                handoff.put("payload"); // blocks until take() is called by someone else
            } catch (InterruptedException ignored) {
            }
            putNanos[0] = System.nanoTime() - start;
        });
        handoffProducer.start();
        Thread.sleep(300); // prove the producer is genuinely blocked with nobody consuming yet
        System.out.println("300ms later (no consumer yet), producer thread state: " + handoffProducer.getState());

        String received = handoff.take(); // only now does put() unblock
        handoffProducer.join();
        System.out.printf("take() received \"%s\"; put() was blocked for ~%d ms until this exact take() call.%n",
                received, putNanos[0] / 1_000_000);
        System.out.println("(SynchronousQueue has no internal storage at all -- put() and take() rendezvous directly,");
        System.out.println(" unlike ArrayBlockingQueue where put() only blocks once the bounded buffer is genuinely full)");
    }
}
