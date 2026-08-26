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
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * The register's answer to its own follow-up: after a bounded number of real retries,
 * a poison message is really published to a real dead-letter topic (with a header
 * recording why), and the consumer really seeks past it and continues -- proving,
 * with real offsets and a real DLQ topic's real contents, that messages after the
 * poison one are no longer blocked once it's routed out of the main flow.
 */
public final class DlqRecoveryDemo {

    private static final String BOOTSTRAP = "localhost:9094";
    private static final String SOURCE_TOPIC = "orders-dlq-source";
    private static final String DLQ_TOPIC = "orders-dlq-target";
    private static final String GROUP = "dlq-recovery-group";
    private static final int MAX_RETRIES_PER_MESSAGE = 3;

    public static void main(String[] args) throws Exception {
        Properties adminProps = new Properties();
        adminProps.put("bootstrap.servers", BOOTSTRAP);
        try (Admin admin = Admin.create(adminProps)) {
            try {
                admin.deleteTopics(List.of(SOURCE_TOPIC, DLQ_TOPIC)).all().get(30, TimeUnit.SECONDS);
                Thread.sleep(2000);
            } catch (Exception ignored) {
            }
            admin.createTopics(List.of(
                    new NewTopic(SOURCE_TOPIC, 1, (short) 1),
                    new NewTopic(DLQ_TOPIC, 1, (short) 1))).all().get();
        }

        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);
        for (int i = 1; i <= 10; i++) {
            String value = (i == 5) ? "INVALID_AMOUNT" : String.valueOf(i * 10.0);
            producer.send(new ProducerRecord<>(SOURCE_TOPIC, "order-" + i, value)).get();
        }
        System.out.println("Produced 10 real messages to " + SOURCE_TOPIC + "; order-5's value is deliberately invalid.");

        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        List<String> processedOk = new ArrayList<>();
        List<String> deadLettered = new ArrayList<>();
        TopicPartition partition = new TopicPartition(SOURCE_TOPIC, 0);
        int retriesOnCurrentRecord = 0;

        System.out.println();
        System.out.println("=== DLQ-aware consumer: retry up to " + MAX_RETRIES_PER_MESSAGE
                + " times, then dead-letter and continue ===");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(Collections.singletonList(SOURCE_TOPIC));

            int roundsWithoutProgress = 0;
            while (processedOk.size() + deadLettered.size() < 10 && roundsWithoutProgress < 10) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(2));
                if (records.isEmpty()) {
                    roundsWithoutProgress++;
                    continue;
                }
                roundsWithoutProgress = 0;
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        double amount = Double.parseDouble(record.value());
                        System.out.println("  Processed " + record.key() + " = $" + amount);
                        processedOk.add(record.key());
                        retriesOnCurrentRecord = 0;
                        consumer.commitSync(Collections.singletonMap(partition,
                                new OffsetAndMetadata(record.offset() + 1)));
                    } catch (NumberFormatException e) {
                        retriesOnCurrentRecord++;
                        System.out.println("  FAILED " + record.key() + " (\"" + record.value()
                                + "\") -- attempt " + retriesOnCurrentRecord + " of " + MAX_RETRIES_PER_MESSAGE);
                        if (retriesOnCurrentRecord >= MAX_RETRIES_PER_MESSAGE) {
                            ProducerRecord<String, String> dlqRecord =
                                    new ProducerRecord<>(DLQ_TOPIC, record.key(), record.value());
                            dlqRecord.headers().add(new RecordHeader("dead-letter-reason",
                                    "NumberFormatException after 3 retries".getBytes()));
                            producer.send(dlqRecord).get();
                            System.out.println("  DEAD-LETTERED " + record.key()
                                    + " to " + DLQ_TOPIC + " after " + MAX_RETRIES_PER_MESSAGE + " real retries; continuing.");
                            deadLettered.add(record.key());
                            retriesOnCurrentRecord = 0;
                            consumer.commitSync(Collections.singletonMap(partition,
                                    new OffsetAndMetadata(record.offset() + 1)));
                            // poll() already advanced the consumer's real fetch position past this
                            // whole batch (Kafka's client sets position at fetch time, not per-record
                            // as the caller iterates) -- seek explicitly so the next poll() actually
                            // starts at order-6 instead of silently skipping straight to the end.
                            consumer.seek(partition, record.offset() + 1);
                        } else {
                            consumer.seek(partition, record.offset());
                        }
                        break; // re-poll so seek (or the next record) takes effect cleanly
                    }
                }
            }
        } finally {
            producer.close();
        }

        System.out.println();
        System.out.println("=== Result ===");
        System.out.println("Successfully processed (" + processedOk.size() + "): " + processedOk);
        System.out.println("Dead-lettered (" + deadLettered.size() + "): " + deadLettered);

        Properties dlqConsumerProps = new Properties();
        dlqConsumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);
        dlqConsumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "dlq-verification-group");
        dlqConsumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        dlqConsumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        dlqConsumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        try (KafkaConsumer<String, String> dlqConsumer = new KafkaConsumer<>(dlqConsumerProps)) {
            dlqConsumer.subscribe(Collections.singletonList(DLQ_TOPIC));
            ConsumerRecords<String, String> dlqRecords = dlqConsumer.poll(Duration.ofSeconds(5));
            System.out.println();
            System.out.println("Real contents of " + DLQ_TOPIC + " (verified by actually consuming it):");
            for (ConsumerRecord<String, String> record : dlqRecords) {
                String reason = new String(record.headers().lastHeader("dead-letter-reason").value());
                System.out.println("  " + record.key() + " = \"" + record.value() + "\" (reason: " + reason + ")");
            }
        }
    }
}
