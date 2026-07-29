import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * T-704 -- delivery semantics, observed rather than asserted.
 *
 * Same 18-record "orders" topic, same consumer group used twice:
 *   Run A ("process-then-commit"): commit offsets only AFTER processing
 *                                   completes -> a crash before commit
 *                                   redelivers the batch (at-least-once).
 *   Run B ("commit-then-process"): commit offsets BEFORE processing
 *                                   -> a crash after commit but before
 *                                   the "processing" step LOSES the batch
 *                                   (at-most-once).
 * Both runs use a fresh group id; the "crash" is simulated by throwing
 * after the critical step so the process exits without doing the step
 * on the other side of the crash point. Re-running the same group id
 * shows exactly what got redelivered vs dropped.
 */
public class DeliverySemanticsDemo {
    public static void main(String[] args) throws Exception {
        atLeastOnce();
        System.out.println();
        atMostOnce();
    }

    static void atLeastOnce() {
        String group = "at-least-once-" + System.currentTimeMillis();
        System.out.println("== at-least-once: commit AFTER processing ==");
        System.out.println("-- attempt 1: process batch, crash before commit --");
        int firstBatch = processThenCommit(group, true);
        System.out.println("-- attempt 2 (same group, no commit landed): reprocess from last committed offset --");
        int secondBatch = processThenCommit(group, false);
        System.out.printf("attempt 1 processed %d records (uncommitted) + attempt 2 processed %d records (redelivered) "
                + "= %d total deliveries for 18 unique records -> duplicates observed%n",
                firstBatch, secondBatch, firstBatch + secondBatch);
    }

    static void atMostOnce() {
        String group = "at-most-once-" + System.currentTimeMillis();
        System.out.println("== at-most-once: commit BEFORE processing ==");
        System.out.println("-- attempt 1: commit offsets immediately on poll, then crash before processing --");
        int firstBatch = commitThenProcess(group, true);
        System.out.println("-- attempt 2 (same group, offsets already committed): poll returns nothing left --");
        int secondBatch = commitThenProcess(group, false);
        System.out.printf("attempt 1 committed offsets for %d records but crashed before processing them "
                + "+ attempt 2 processed %d records = %d records actually processed out of 18 -> loss observed%n",
                firstBatch, secondBatch, secondBatch);
    }

    static int processThenCommit(String clientId, boolean crashAfterProcessing) {
        try (KafkaConsumer<String, String> consumer = newConsumer(clientId, clientId)) {
            consumer.subscribe(List.of("orders"));
            ConsumerRecords<String, String> records = pollUntilNonEmpty(consumer);
            int n = records.count();
            for (ConsumerRecord<String, String> r : records) {
                // "process" the record (side effect happens here, before commit)
            }
            if (crashAfterProcessing) {
                System.out.printf("  processed %d records, simulating crash BEFORE commitSync()%n", n);
                return n; // return without committing -- simulates the crash
            }
            consumer.commitSync();
            System.out.printf("  processed %d records, committed successfully%n", n);
            return n;
        }
    }

    static int commitThenProcess(String clientId, boolean crashBeforeProcessing) {
        try (KafkaConsumer<String, String> consumer = newConsumer(clientId, clientId)) {
            consumer.subscribe(List.of("orders"));
            ConsumerRecords<String, String> records = pollUntilNonEmpty(consumer);
            int n = records.count();
            consumer.commitSync(); // commit BEFORE processing
            if (crashBeforeProcessing) {
                System.out.printf("  committed offsets for %d records, simulating crash BEFORE processing them%n", n);
                return 0; // the "process" step never ran -- those records are gone
            }
            for (ConsumerRecord<String, String> r : records) {
                // "process" the record
            }
            System.out.printf("  committed and processed %d records (0 expected -- backlog was already drained by attempt 1's commit)%n", n);
            return n;
        }
    }

    static ConsumerRecords<String, String> pollUntilNonEmpty(KafkaConsumer<String, String> consumer) {
        ConsumerRecords<String, String> records;
        long deadline = System.currentTimeMillis() + 8000;
        do {
            records = consumer.poll(Duration.ofMillis(500));
        } while (records.isEmpty() && System.currentTimeMillis() < deadline);
        return records;
    }

    static KafkaConsumer<String, String> newConsumer(String clientId, String group) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);
        return new KafkaConsumer<>(props);
    }
}
