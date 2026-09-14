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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Real proof of the two foundational messaging patterns -- point-to-point
 * (competing consumers: each message consumed exactly once across a group) and
 * publish-subscribe (each independent subscriber gets every message) -- using the
 * IDENTICAL topic and the IDENTICAL 10 real messages, differing only in how the
 * consumers are grouped. This is the general Enterprise-Integration-Patterns
 * distinction, not Kafka-specific rebalancing mechanics (covered separately in
 * consumer-groups-and-rebalancing.md and consumer-lag-backpressure-and-dlq-strategy.md).
 */
public final class PointToPointVsPubSubDemo {

    private static final String BOOTSTRAP = "localhost:9094";
    private static final int MESSAGE_COUNT = 10;

    public static void main(String[] args) throws Exception {
        runPointToPoint();
        System.out.println();
        runPublishSubscribe();
    }

    private static void runPointToPoint() throws Exception {
        String topic = "orders-p2p";
        createTopicAndProduce(topic);

        System.out.println("=== Point-to-point (competing consumers): 3 real consumers, SAME group \"order-processors\" ===");
        ConcurrentHashMap<Integer, AtomicInteger> receivedByConsumer = new ConcurrentHashMap<>();
        for (int i = 0; i < 3; i++) receivedByConsumer.put(i, new AtomicInteger());
        AtomicInteger totalReceived = new AtomicInteger();

        CountDownLatch latch = new CountDownLatch(3);
        for (int i = 0; i < 3; i++) {
            final int id = i;
            new Thread(() -> {
                try (KafkaConsumer<String, String> consumer = subscribe(topic, "order-processors")) {
                    long deadline = System.currentTimeMillis() + 8000;
                    while (System.currentTimeMillis() < deadline) {
                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                        receivedByConsumer.get(id).addAndGet(records.count());
                        totalReceived.addAndGet(records.count());
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        latch.await(15, TimeUnit.SECONDS);

        receivedByConsumer.forEach((id, count) -> System.out.println("  consumer-" + id + " received: " + count.get()));
        System.out.println("Real total received across the whole group: " + totalReceived.get()
                + " (expected " + MESSAGE_COUNT + " -- each message consumed exactly ONCE across the group)");
    }

    private static void runPublishSubscribe() throws Exception {
        String topic = "orders-pubsub";
        createTopicAndProduce(topic);

        System.out.println("=== Publish-subscribe: 3 real consumers, 3 DIFFERENT groups ===");
        String[] groups = {"email-service", "inventory-service", "analytics-service"};
        ConcurrentHashMap<String, AtomicInteger> receivedByGroup = new ConcurrentHashMap<>();
        for (String g : groups) receivedByGroup.put(g, new AtomicInteger());

        CountDownLatch latch = new CountDownLatch(groups.length);
        for (String group : groups) {
            new Thread(() -> {
                try (KafkaConsumer<String, String> consumer = subscribe(topic, group)) {
                    long deadline = System.currentTimeMillis() + 8000;
                    while (System.currentTimeMillis() < deadline) {
                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                        receivedByGroup.get(group).addAndGet(records.count());
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        latch.await(15, TimeUnit.SECONDS);

        receivedByGroup.forEach((group, count) ->
                System.out.println("  " + group + " received: " + count.get() + " (expected " + MESSAGE_COUNT + " -- its OWN independent copy of every message)"));
    }

    private static void createTopicAndProduce(String topic) throws Exception {
        Properties adminProps = new Properties();
        adminProps.put("bootstrap.servers", BOOTSTRAP);
        try (Admin admin = Admin.create(adminProps)) {
            try {
                admin.deleteTopics(List.of(topic)).all().get(30, TimeUnit.SECONDS);
                Thread.sleep(1500);
            } catch (Exception ignored) {
            }
            admin.createTopics(List.of(new NewTopic(topic, 1, (short) 1))).all().get();
        }

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
            for (int i = 1; i <= MESSAGE_COUNT; i++) {
                producer.send(new ProducerRecord<>(topic, "order-" + i, "payload-" + i)).get();
            }
        }
    }

    private static KafkaConsumer<String, String> subscribe(String topic, String group) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of(topic));
        return consumer;
    }
}
