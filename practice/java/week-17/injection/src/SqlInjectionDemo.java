import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

// Real demo against a live PostgreSQL 16 instance: classic auth-bypass SQL
// injection via string concatenation, and the fix via PreparedStatement.
public class SqlInjectionDemo {

    static final String URL = "jdbc:postgresql://127.0.0.1:15432/appdb";

    // VULNERABLE: builds SQL by string concatenation.
    static boolean loginVulnerable(Connection conn, String username, String passwordHash) throws Exception {
        String sql = "SELECT * FROM users WHERE username = '" + username
                + "' AND password_hash = '" + passwordHash + "'";
        System.out.println("  executed SQL: " + sql);
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next();
        }
    }

    // FIXED: parameters are bound, never interpolated into the SQL text.
    static boolean loginFixed(Connection conn, String username, String passwordHash) throws Exception {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, "postgres", "demo")) {
            String attackerUsername = "admin' --";
            String attackerPassword = "anything";

            System.out.println("=== VULNERABLE login: username=\"" + attackerUsername + "\" ===");
            boolean bypassed = loginVulnerable(conn, attackerUsername, attackerPassword);
            System.out.println("  login succeeded (no valid password given)? " + bypassed);

            System.out.println();
            System.out.println("=== FIXED login: same attacker input, PreparedStatement ===");
            boolean blocked = loginFixed(conn, attackerUsername, attackerPassword);
            System.out.println("  login succeeded? " + blocked + "  (username literally \"admin' --\" doesn't exist)");

            System.out.println();
            System.out.println("=== FIXED login: legitimate alice credentials ===");
            boolean legit = loginFixed(conn, "alice", "hash1");
            System.out.println("  login succeeded? " + legit);
        }
    }
}
