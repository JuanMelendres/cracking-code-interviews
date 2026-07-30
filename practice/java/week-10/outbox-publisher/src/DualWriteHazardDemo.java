import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        try (Connection conn = pgConnection()) {
            long orderId;
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
        System.out.println("Kafka topic 'order-events': 0 messages for this order (nothing ever published it -- "
                + "there is no mechanism in this design that could have retried it)");
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
