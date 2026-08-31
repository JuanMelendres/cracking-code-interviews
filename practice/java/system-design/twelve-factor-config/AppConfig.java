import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

// A real, minimal config loader implementing 12-factor config precedence:
// defaults < config file < environment variables < command-line arguments.
// Higher layers override lower ones for the same key -- the identical real
// ordering Spring Boot's own PropertySource resolution uses.
public class AppConfig {
    private final Map<String, String> resolved = new HashMap<>();
    private final Map<String, String> sourceOf = new HashMap<>();

    public static AppConfig load(String propertiesFilePath, String[] cliArgs) throws IOException {
        AppConfig config = new AppConfig();

        // Layer 1: defaults, hardcoded, lowest precedence.
        config.set("timeout.ms", "1000", "default");
        config.set("max.retries", "3", "default");
        config.set("service.name", "unnamed-service", "default");

        // Layer 2: a real config file, if present.
        Path path = Path.of(propertiesFilePath);
        if (Files.exists(path)) {
            Properties fileProps = new Properties();
            try (InputStream in = Files.newInputStream(path)) {
                fileProps.load(in);
            }
            for (String key : fileProps.stringPropertyNames()) {
                config.set(key, fileProps.getProperty(key), "file:" + propertiesFilePath);
            }
        }

        // Layer 3: real OS environment variables (APP_TIMEOUT_MS style, mapped to timeout.ms).
        for (Map.Entry<String, String> env : System.getenv().entrySet()) {
            if (env.getKey().startsWith("APP_")) {
                String key = env.getKey().substring("APP_".length()).toLowerCase().replace('_', '.');
                config.set(key, env.getValue(), "env:" + env.getKey());
            }
        }

        // Layer 4: real command-line arguments (--key=value), highest precedence.
        for (String arg : cliArgs) {
            if (arg.startsWith("--") && arg.contains("=")) {
                String[] parts = arg.substring(2).split("=", 2);
                config.set(parts[0], parts[1], "cli:" + arg);
            }
        }

        return config;
    }

    private void set(String key, String value, String source) {
        resolved.put(key, value);
        sourceOf.put(key, source);
    }

    public String get(String key) {
        return resolved.get(key);
    }

    public String sourceOf(String key) {
        return sourceOf.get(key);
    }

    public Map<String, String> asMap() {
        return resolved;
    }
}
