import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

/**
 * T-618 -- the problem the outbox pattern exists to solve, reproduced
 * directly: write the business row to Postgres (committed, durable),
 * THEN separately publish to Kafka -- and simulate a crash in between.
 * No transaction spans both systems, so there is no way to make this
 * atomic without the outbox pattern.
 */
public class DualWriteHazardDemo {
    static final String PG_URL = "jdbc:postgresql://localhost:5433/week10";

    public static void main(String[] args) throws Exception {
        System.out.println("== dual write, no outbox: DB commit succeeds, then \"crash\" before the Kafka publish ==");

        long orderId;
        try (Connection conn = pgConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO orders (customer_id, amount_cents) VALUES (?, ?) RETURNING id")) {
                ps.setString(1, "dual-write-hazard-customer");
                ps.setLong(2, 4999);
                ResultSet rs = ps.executeQuery();
                rs.next();
                orderId = rs.getLong(1);
            }
            System.out.println("Order " + orderId + " COMMITTED to Postgres, durable, visible to any other reader right now.");

            // simulate the crash: the process dies here, before the Kafka
            // publish call ever executes -- deliberately no try/catch,
            // this represents an unrecoverable failure (OOM, kill -9,
            // deploy during this exact window), not a caught exception
            System.out.println("Simulating a crash HERE -- before any Kafka publish call is even attempted.");
            System.out.println("(In the no-outbox design, nothing else in the system knows this order needs an event published.");
            System.out.println(" There is no queue, no retry, no record of the intent -- the event is simply gone.)");
        }

        System.out.println();
        System.out.println("== verifying the order exists but no event was ever published anywhere ==");
        try (Connection conn = pgConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT count(*) FROM orders WHERE customer_id = 'dual-write-hazard-customer'")) {
            ResultSet rs = ps.executeQuery();
            rs.next();
            System.out.println("orders rows for this customer: " + rs.getInt(1) + " (the business write DID survive)");
        }
        int actualMessageCount = countMessagesForKey(String.valueOf(orderId));
        System.out.println("Kafka topic 'order-events': " + actualMessageCount + " messages with key=" + orderId
                + " (actually queried Kafka, not asserted -- nothing ever published it, "
                + "there is no mechanism in this design that could have retried it)");
    }

    /** Actually queries Kafka rather than asserting the count -- consumes order-events
     * from the beginning and counts messages keyed to this specific order. */
    static int countMessagesForKey(String key) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "dual-write-verify-" + System.currentTimeMillis());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(List.of("order-events"));
            int count = 0;
            long deadline = System.currentTimeMillis() + 5000;
            while (System.currentTimeMillis() < deadline) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                for (var r : records) {
                    if (key.equals(r.key())) count++;
                }
            }
            return count;
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
}
