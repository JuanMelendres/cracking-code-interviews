import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * T-618 -- the polling publisher half of the outbox pattern. Polls
 * outbox for unpublished rows, publishes each to Kafka, marks it
 * published ONLY after the Kafka send is confirmed. Supports
 * --crash-after-first-publish to simulate a process death in the gap
 * between "Kafka publish confirmed" and "DB row marked published" --
 * the one window this design does NOT make atomic, and the reason the
 * outbox pattern gives at-least-once delivery, not exactly-once.
 */
public class OutboxPoller {
    static final String PG_URL = "jdbc:postgresql://localhost:5433/week10";
    static final String TOPIC = "order-events";

    public static void main(String[] args) throws Exception {
        boolean crashAfterFirstPublish = args.length > 0 && args[0].equals("--crash-after-first-publish");

        try (Connection conn = pgConnection();
             KafkaProducer<String, String> producer = kafkaProducer()) {

            int published = 0;
            try (PreparedStatement selectStmt = conn.prepareStatement(
                    "SELECT id, aggregate_id, event_type, payload FROM outbox WHERE published = false ORDER BY id");
                 ResultSet rs = selectStmt.executeQuery()) {

                while (rs.next()) {
                    long outboxId = rs.getLong("id");
                    long aggregateId = rs.getLong("aggregate_id");
                    String eventType = rs.getString("event_type");
                    String payload = rs.getString("payload");

                    producer.send(new ProducerRecord<>(TOPIC, String.valueOf(aggregateId), payload)).get();
                    System.out.println("Published outbox row " + outboxId + " (" + eventType + ", aggregate=" + aggregateId + ") to Kafka.");
                    published++;

                    if (crashAfterFirstPublish && published == 1) {
                        System.out.println("Simulating a crash HERE -- Kafka publish confirmed, but BEFORE marking outbox row "
                                + outboxId + " as published in Postgres.");
                        System.exit(1);
                    }

                    try (PreparedStatement markStmt = conn.prepareStatement(
                            "UPDATE outbox SET published = true, published_at = now() WHERE id = ?")) {
                        markStmt.setLong(1, outboxId);
                        markStmt.executeUpdate();
                    }
                }
            }
            System.out.println("Poller pass complete: " + published + " row(s) published this pass.");
        }
    }

    static Connection pgConnection() throws Exception {
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");
        Connection conn = DriverManager.getConnection(PG_URL, props);
        conn.setAutoCommit(true);
        return conn;
    }

    static KafkaProducer<String, String> kafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        return new KafkaProducer<>(props);
    }
}
