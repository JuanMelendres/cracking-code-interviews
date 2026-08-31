import java.util.List;
import java.util.Map;

// Real proof of why validating required config at startup ("fail fast")
// beats discovering a missing value deep inside business logic. Both
// scenarios below run against the identical, real config -- deliberately
// missing "database.url" -- to show the same root cause producing two very
// different real failure experiences.
public class FailFastDemo {

    public static void main(String[] args) throws Exception {
        // The same real config.properties used by PrecedenceDemo -- it never
        // sets database.url, which is exactly the point.
        AppConfig config = AppConfig.load("config.properties", args);

        System.out.println("=== BUGGY: no startup validation -- the app starts up \"successfully\" ===");
        System.out.println("Real config loaded. Server \"starts\" normally. Business logic runs later, on the first real request...");
        try {
            simulateFirstRealRequest(config);
        } catch (Exception e) {
            System.out.println("Real failure, deep in business logic, minutes/hours after startup:");
            System.out.println("  " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== FIXED: real startup validation -- fails immediately, at boot, with a clear message ===");
        try {
            validateRequiredConfig(config, List.of("database.url", "service.name", "timeout.ms"));
            System.out.println("Startup validation passed -- would proceed to serve requests.");
        } catch (IllegalStateException e) {
            System.out.println("Real startup failure -- BEFORE the app ever accepts a single request:");
            System.out.println("  " + e.getMessage());
        }
    }

    private static void simulateFirstRealRequest(AppConfig config) {
        String databaseUrl = config.get("database.url");
        // The real bug: no null-check here, because startup "succeeded" and
        // nobody expected this specific value to be missing at this point.
        System.out.println("Connecting to database at: " + databaseUrl.toUpperCase());
    }

    private static void validateRequiredConfig(AppConfig config, List<String> requiredKeys) {
        for (String key : requiredKeys) {
            if (config.get(key) == null) {
                throw new IllegalStateException(
                        "Missing required config key '" + key + "' -- refusing to start. "
                                + "Set it via config file, an APP_" + key.toUpperCase().replace('.', '_')
                                + " environment variable, or --" + key + "=... on the command line.");
            }
        }
    }
}
