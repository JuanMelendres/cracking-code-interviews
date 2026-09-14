import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Real proof of the register's own follow-up: "one bad message blocks the partition
 * -- options?" Ten real messages are produced to a real single-partition topic;
 * message 5's value ("INVALID_AMOUNT") really fails to parse as a double. A naive
 * consumer that retries a failed record in place -- never committing past it, never
 * routing it anywhere else -- really never processes messages 6-10, no matter how
 * many retry rounds run, because Kafka partition ordering means nothing after an
 * uncommitted offset can be delivered ahead of it.
 */
public final class PoisonMessagePartitionBlockingDemo {

    private static final String BOOTSTRAP = "localhost:9094";
    private static final String TOPIC = "orders-poison-block";
    private static final String GROUP = "poison-block-group";

    public static void main(String[] args) throws Exception {
        Properties adminProps = new Properties();
        adminProps.put("bootstrap.servers", BOOTSTRAP);
        try (Admin admin = Admin.create(adminProps)) {
            admin.deleteTopics(List.of(TOPIC)).all().get(30, java.util.concurrent.TimeUnit.SECONDS);
            Thread.sleep(2000);
            admin.createTopics(List.of(new NewTopic(TOPIC, 1, (short) 1))).all().get();
        } catch (Exception ignored) {
            // topic may not have existed on first run
        }

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {
            for (int i = 1; i <= 10; i++) {
                String value = (i == 5) ? "INVALID_AMOUNT" : String.valueOf(i * 10.0);
                producer.send(new ProducerRecord<>(TOPIC, "order-" + i, value)).get();
            }
        }
        System.out.println("Produced 10 real messages to " + TOPIC + "; order-5's value is deliberately invalid: \"INVALID_AMOUNT\"");

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        int successfullyProcessed = 0;
        int retryRounds = 0;
        TopicPartition partition = new TopicPartition(TOPIC, 0);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(Collections.singletonList(TOPIC));

            System.out.println();
            System.out.println("=== Naive consumer: retry failed record in place, never route it elsewhere ===");
            for (retryRounds = 1; retryRounds <= 5; retryRounds++) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(3));
                boolean hitPoisonThisRound = false;
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        double amount = Double.parseDouble(record.value());
                        System.out.println("  Round " + retryRounds + ": processed " + record.key() + " = $" + amount);
                        successfullyProcessed++;
                        consumer.commitSync(Collections.singletonMap(
                                new TopicPartition(record.topic(), record.partition()),
                                new OffsetAndMetadata(record.offset() + 1)));
                    } catch (NumberFormatException e) {
                        System.out.println("  Round " + retryRounds + ": FAILED to process " + record.key()
                                + " (\"" + record.value() + "\") -- " + e.getClass().getSimpleName()
                                + ". Naive consumer does NOT commit past it and will retry.");
                        hitPoisonThisRound = true;
                        break; // stop processing this batch -- don't skip ahead of the poison message
                    }
                }
                if (hitPoisonThisRound) {
                    consumer.seek(partition, consumer.committed(Collections.singleton(partition))
                            .get(partition).offset());
                }
            }

            long endOffset = consumer.endOffsets(Collections.singletonList(partition)).get(partition);
            long committedOffset = consumer.committed(Collections.singleton(partition)).get(partition) == null
                    ? 0 : consumer.committed(Collections.singleton(partition)).get(partition).offset();
            long realLag = endOffset - committedOffset;

            System.out.println();
            System.out.println("=== Result after " + (retryRounds - 1) + " real retry rounds ===");
            System.out.println("Messages successfully processed: " + successfullyProcessed + " of 10 (order-1 through order-4)");
            System.out.println("Committed offset stuck at: " + committedOffset + " (order-5's offset)");
            System.out.println("Real measured consumer lag: " + realLag
                    + " (order-5 through order-10 -- all unreachable while order-5 blocks the partition)");
        }
    }
}
