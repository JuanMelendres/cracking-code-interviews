import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Real proof of the register's own named misconception: "that adding consumers
 * beyond partition count helps." A real topic with 3 partitions gets 5 real,
 * independent consumer instances in the same consumer group. Kafka's own
 * partition-assignment protocol -- not a simulated one -- really assigns at most one
 * partition per consumer within a group, so with 3 partitions and 5 consumers, 2 real
 * consumer instances are really left with zero partitions and process zero messages,
 * no matter how long they run.
 */
public final class ConsumersExceedPartitionsDemo {

    private static final String BOOTSTRAP = "localhost:9094";
    private static final String TOPIC = "orders-three-partitions";
    private static final String GROUP = "exceed-partitions-group";
    private static final int PARTITION_COUNT = 3;
    private static final int CONSUMER_COUNT = 5;

    public static void main(String[] args) throws Exception {
        Properties adminProps = new Properties();
        adminProps.put("bootstrap.servers", BOOTSTRAP);
        try (Admin admin = Admin.create(adminProps)) {
            try {
                admin.deleteTopics(List.of(TOPIC)).all().get(30, TimeUnit.SECONDS);
                Thread.sleep(2000);
            } catch (Exception ignored) {
            }
            admin.createTopics(List.of(new NewTopic(TOPIC, PARTITION_COUNT, (short) 1))).all().get();
        }

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
            for (int i = 1; i <= 30; i++) {
                producer.send(new ProducerRecord<>(TOPIC, "order-" + i, "value-" + i)).get();
            }
        }
        System.out.println("Produced 30 real messages, round-robin across " + PARTITION_COUNT + " real partitions.");

        System.out.println("=== Real topic \"" + TOPIC + "\" created with " + PARTITION_COUNT + " partitions ===");
        System.out.println("=== Starting " + CONSUMER_COUNT + " real, independent consumer instances in group \"" + GROUP + "\" ===");
        System.out.println();

        CountDownLatch allAssigned = new CountDownLatch(CONSUMER_COUNT);
        AtomicInteger[] messagesReceivedByConsumer = new AtomicInteger[CONSUMER_COUNT];
        Thread[] threads = new Thread[CONSUMER_COUNT];

        for (int i = 0; i < CONSUMER_COUNT; i++) {
            final int consumerIndex = i;
            messagesReceivedByConsumer[i] = new AtomicInteger(0);
            threads[i] = new Thread(() -> {
                Properties consumerProps = new Properties();
                consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
                consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);
                consumerProps.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer-" + consumerIndex);
                consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
                consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

                try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
                    consumer.subscribe(Collections.singletonList(TOPIC));
                    long deadline = System.currentTimeMillis() + 15000;
                    boolean reported = false;
                    while (System.currentTimeMillis() < deadline) {
                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                        messagesReceivedByConsumer[consumerIndex].addAndGet(records.count());
                        if (!reported && !consumer.assignment().isEmpty()) {
                            System.out.println("consumer-" + consumerIndex + ": real assignment = " + consumer.assignment());
                            reported = true;
                            allAssigned.countDown();
                        }
                    }
                    if (!reported) {
                        System.out.println("consumer-" + consumerIndex + ": real assignment = [] (IDLE -- no partitions to assign)");
                        allAssigned.countDown();
                    }
                } catch (Exception e) {
                    System.out.println("consumer-" + consumerIndex + ": error " + e);
                    allAssigned.countDown();
                }
            });
            threads[i].start();
        }

        allAssigned.await(20, TimeUnit.SECONDS);
        for (Thread t : threads) {
            t.join();
        }

        System.out.println();
        System.out.println("=== Result: messages received per consumer (after real rebalance settled) ===");
        int idleCount = 0;
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            int received = messagesReceivedByConsumer[i].get();
            System.out.println("consumer-" + i + ": " + received + " messages received"
                    + (received == 0 ? "  <-- IDLE" : ""));
            if (received == 0) idleCount++;
        }
        System.out.println();
        System.out.println(idleCount + " of " + CONSUMER_COUNT + " consumers received zero messages -- "
                + "real proof that adding consumers beyond the partition count (" + PARTITION_COUNT
                + ") does not increase real parallelism.");
    }
}
