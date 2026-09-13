import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/** The dependency an integration test exercises for real -- a real Postgres, not a mock. */
public class OrderRepository {
    private final String jdbcUrl;

    public OrderRepository(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public long insert(String customerId, long amountCents) throws Exception {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO test_orders (customer_id, amount_cents) VALUES (?, ?) RETURNING id")) {
            ps.setString(1, customerId);
            ps.setLong(2, amountCents);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getLong(1);
        }
    }

    public long findAmountById(long id) throws Exception {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT amount_cents FROM test_orders WHERE id = ?")) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getLong(1);
        }
    }

    private Connection connect() throws Exception {
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");
        return DriverManager.getConnection(jdbcUrl, props);
    }
}
