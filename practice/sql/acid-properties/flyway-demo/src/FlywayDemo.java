import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.FlywayException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Real proof of how Flyway actually tracks schema state: a real
 * flyway_schema_history table (not a description of one), and a real
 * checksum-mismatch failure when an already-applied migration file is
 * edited after the fact.
 */
public class FlywayDemo {

    static final String URL = "jdbc:postgresql://localhost:5433/flywaylab";
    static final String USER = "postgres";
    static final String PASS = "postgres";

    public static void main(String[] args) throws Exception {
        Flyway flyway = Flyway.configure()
                .dataSource(URL, USER, PASS)
                .locations("filesystem:migrations")
                .load();

        System.out.println("== First run: apply V1 and V2 from a clean database ==");
        flyway.migrate();
        printHistory();

        System.out.println();
        System.out.println("== Running migrate() AGAIN with no changes: real no-op, nothing re-applied ==");
        var result = flyway.migrate();
        System.out.println("Migrations executed this run: " + result.migrationsExecuted + "  (expect 0 -- V1/V2 already applied)");

        System.out.println();
        System.out.println("== Tampering with V1's file content AFTER it was already applied ==");
        Path v1 = Path.of("migrations/V1__create_accounts.sql");
        String original = Files.readString(v1, StandardCharsets.UTF_8);
        Files.writeString(v1, original + "\n-- a tampered, unauthorized edit to an already-applied migration\n");

        try {
            flyway.migrate();
            System.out.println("Migrated without error (unexpected)");
        } catch (FlywayException e) {
            System.out.println("Real FlywayException: " + e.getMessage());
        } finally {
            Files.writeString(v1, original); // restore, so re-running this demo stays reproducible
        }
    }

    static void printHistory() throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT installed_rank, version, description, checksum, success FROM flyway_schema_history ORDER BY installed_rank")) {
            System.out.println("Real flyway_schema_history table contents:");
            while (rs.next()) {
                System.out.printf("  rank=%d version=%s desc=%-20s checksum=%d success=%b%n",
                        rs.getInt("installed_rank"), rs.getString("version"), rs.getString("description"),
                        rs.getInt("checksum"), rs.getBoolean("success"));
            }
        }
    }
}
