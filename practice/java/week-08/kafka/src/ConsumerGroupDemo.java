import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * T-703 -- consumer groups: partition assignment splits across group
 * members, and a member that leaves triggers a rebalance onto the
 * survivor(s). Topic "orders" has 4 partitions (created by
 * ProducerPartitionKeyDemo) and already has 18 records on it from that run.
 */
public class ConsumerGroupDemo {
    static final String TOPIC = "orders";
    // fresh group id per run so "earliest" always replays the full backlog
    static final String GROUP = "order-processors-" + System.currentTimeMillis();

    public static void main(String[] args) throws Exception {
        System.out.println("== consumer-1 joins group '" + GROUP + "' alone -> gets all 4 partitions ==");
        AtomicInteger c1Count = new AtomicInteger();
        AtomicInteger c2Count = new AtomicInteger();

        Thread t1 = new Thread(() -> runConsumer("consumer-1", c1Count, 9000));
        t1.start();
        Thread.sleep(4000); // let consumer-1's initial join fully settle (group-coordinator discovery + join + sync)
        System.out.println("== consumer-2 joins the same group -> triggers rebalance, partitions split ==");
        Thread t2 = new Thread(() -> runConsumer("consumer-2", c2Count, 5000));
        t2.start();
        t1.join();
        t2.join();

        System.out.printf("consumer-1 processed %d records, consumer-2 processed %d records%n",
                c1Count.get(), c2Count.get());

        System.out.println();
        System.out.println("== both leave; a solo consumer-3 joins the same group -> full rebalance, gets all partitions back ==");
        AtomicInteger c3Count = new AtomicInteger();
        runConsumer("consumer-3", c3Count, 6000);
        System.out.printf("consumer-3 alone was assigned: processed %d NEW records (rest already committed by consumer-1/2)%n", c3Count.get());
    }

    static void runConsumer(String clientId, AtomicInteger counter, long runMillis) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of(TOPIC));
            long deadline = System.currentTimeMillis() + runMillis;
            Set<TopicPartition> lastAssignment = Set.of();
            while (System.currentTimeMillis() < deadline) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
                if (!consumer.assignment().equals(lastAssignment) && !consumer.assignment().isEmpty()) {
                    lastAssignment = Set.copyOf(consumer.assignment());
                    System.out.printf("[%s] assigned partitions: %s%n", clientId, lastAssignment);
                }
                for (ConsumerRecord<String, String> r : records) {
                    counter.incrementAndGet();
                }
                if (!records.isEmpty()) consumer.commitSync();
            }
        }
    }
}
