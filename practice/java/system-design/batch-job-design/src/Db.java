import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A real H2 in-memory database, shared across the whole JVM for the duration of
 * the demo. Everything below runs against genuine SQL with genuine transactions
 * -- the conditional UPDATE used for the job lock and the checkpoint commit are
 * the actual mechanisms a production job would use, not simulations of them.
 */
final class Db {

    static final String URL = "jdbc:h2:mem:batchdemo;DB_CLOSE_DELAY=-1";

    private Db() {
    }

    static Connection open() throws SQLException {
        Connection c = DriverManager.getConnection(URL, "sa", "");
        c.setAutoCommit(false);
        return c;
    }

    static void reset() throws SQLException {
        try (Connection c = open(); Statement s = c.createStatement()) {
            s.execute("DROP ALL OBJECTS");
            s.execute("""
                    CREATE TABLE invoice (
                        id          INT PRIMARY KEY,
                        amount      INT NOT NULL,
                        processed   BOOLEAN NOT NULL DEFAULT FALSE
                    )""");
            // Every unit of work appends exactly one row here. Counting rows
            // is how the demo proves duplicate or lost work, rather than
            // trusting the job's own reporting.
            s.execute("""
                    CREATE TABLE side_effect (
                        seq        INT AUTO_INCREMENT PRIMARY KEY,
                        invoice_id INT NOT NULL,
                        worker     VARCHAR(32) NOT NULL
                    )""");
            s.execute("""
                    CREATE TABLE job_lock (
                        name       VARCHAR(64) PRIMARY KEY,
                        held_by    VARCHAR(32),
                        locked_at  TIMESTAMP
                    )""");
            s.execute("""
                    CREATE TABLE job_checkpoint (
                        name            VARCHAR(64) PRIMARY KEY,
                        last_done_id    INT NOT NULL
                    )""");
            c.commit();
        }
    }

    static void seedInvoices(int count) throws SQLException {
        try (Connection c = open();
             var ps = c.prepareStatement("INSERT INTO invoice (id, amount) VALUES (?, ?)")) {
            for (int i = 1; i <= count; i++) {
                ps.setInt(1, i);
                ps.setInt(2, i * 10);
                ps.addBatch();
            }
            ps.executeBatch();
            c.commit();
        }
    }

    static int count(String sql) throws SQLException {
        try (Connection c = open(); Statement s = c.createStatement(); var rs = s.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
