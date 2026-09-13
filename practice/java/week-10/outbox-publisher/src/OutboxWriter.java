import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * T-618 -- writes the business row AND the outbox row in ONE Postgres
 * transaction. This is the entire mechanism: by construction, either
 * both rows exist or neither does -- there is no window where the order
 * exists but the outbox event doesn't (or vice versa), because Postgres
 * itself enforces the atomicity, not application coordination.
 */
public class OutboxWriter {
    static final String PG_URL = "jdbc:postgresql://localhost:5433/week10";

    public static void main(String[] args) throws Exception {
        int count = args.length > 0 ? Integer.parseInt(args[0]) : 5;
        try (Connection conn = pgConnection()) {
            conn.setAutoCommit(false);
            for (int i = 0; i < count; i++) {
                long orderId;
                try (PreparedStatement orderStmt = conn.prepareStatement(
                        "INSERT INTO orders (customer_id, amount_cents) VALUES (?, ?) RETURNING id")) {
                    orderStmt.setString(1, "outbox-customer-" + i);
                    orderStmt.setLong(2, 1000 + i);
                    ResultSet rs = orderStmt.executeQuery();
                    rs.next();
                    orderId = rs.getLong(1);
                }
                try (PreparedStatement outboxStmt = conn.prepareStatement(
                        "INSERT INTO outbox (aggregate_id, event_type, payload) VALUES (?, ?, ?)")) {
                    outboxStmt.setLong(1, orderId);
                    outboxStmt.setString(2, "OrderCreated");
                    outboxStmt.setString(3, "{\"orderId\":" + orderId + ",\"customerId\":\"outbox-customer-" + i + "\"}");
                    outboxStmt.executeUpdate();
                }
                conn.commit(); // ONE transaction, both rows -- this is the whole mechanism
                System.out.println("Committed order " + orderId + " + its outbox row, atomically, in one transaction.");
            }
        }
    }

    static Connection pgConnection() throws Exception {
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");
        return DriverManager.getConnection(PG_URL, props);
    }
}
