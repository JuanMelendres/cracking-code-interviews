import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SystemEnvironmentPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Real proof of two things @Value alone cannot do:
 *  1. Relaxed binding -- a kebab-case/environment-variable-style key binds
 *     to a camelCase field, and a NESTED prefix binds into a nested record,
 *     with zero manual parsing.
 *  2. A malformed value fails the WHOLE bind with one exception naming
 *     every offending property, at context-startup time -- not scattered
 *     one-by-one across every @Value injection point that happens to use it.
 */
public class ConfigurationPropertiesDemo {

    @ConfigurationProperties(prefix = "app")
    public record AppProperties(String name, int maxRetries, Retry retry) {
        public record Retry(int maxAttempts, long backoffMs) {}
    }

    @Configuration
    @EnableConfigurationProperties(AppProperties.class)
    static class AppConfig {
    }

    static class Holder {
        @Autowired
        AppProperties props;
    }

    public static void main(String[] args) {
        System.out.println("== Relaxed binding: kebab-case keys -> camelCase record fields, nested prefix -> nested record ==");
        runWithSource(new MapPropertySource("test-file", validProperties()), true);

        System.out.println();
        System.out.println("== Relaxed binding: top-level fields ALSO bind from real environment-variable-shaped keys ==");
        runWithSource(new SystemEnvironmentPropertySource("test-env", envStyleProperties()), false);

        System.out.println();
        System.out.println("== A malformed value fails the WHOLE bind, at context-startup time ==");
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
            ctx.getEnvironment().getPropertySources()
                    .addFirst(new MapPropertySource("test-broken", brokenProperties()));
            ctx.register(AppConfig.class, Holder.class);
            ctx.refresh();
            System.out.println("Bound without error (unexpected): " + ctx.getBean(Holder.class).props);
        } catch (Exception e) {
            Throwable cause = e;
            while (cause.getCause() != null && !(cause instanceof BindException)) {
                cause = cause.getCause();
            }
            System.out.println("Real bind failure: " + cause.getClass().getSimpleName() + ": " + cause.getMessage());
        }
    }

    private static void runWithSource(PropertySource<?> source, boolean printNested) {
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()) {
            ctx.getEnvironment().getPropertySources().addFirst(source);
            ctx.register(AppConfig.class, Holder.class);
            ctx.refresh();
            AppProperties props = ctx.getBean(Holder.class).props;
            System.out.println("Bound record: " + props);
            if (printNested) {
                System.out.println("props.retry().maxAttempts() = " + props.retry().maxAttempts()
                        + "  (nested prefix app.retry.* bound into a nested record, no manual parsing)");
            }
        }
    }

    private static Map<String, Object> validProperties() {
        Map<String, Object> m = new HashMap<>();
        m.put("app.name", "order-service");
        m.put("app.max-retries", "3");
        m.put("app.retry.max-attempts", "5");
        m.put("app.retry.backoff-ms", "250");
        return m;
    }

    private static Map<String, Object> envStyleProperties() {
        Map<String, Object> m = new HashMap<>();
        m.put("APP_NAME", "order-service");
        m.put("APP_MAX_RETRIES", "3");
        return m;
    }

    private static Map<String, Object> brokenProperties() {
        Map<String, Object> m = new HashMap<>();
        m.put("app.name", "order-service");
        m.put("app.max-retries", "not-a-number"); // real type mismatch: int field, non-numeric value
        m.put("app.retry.max-attempts", "5");
        m.put("app.retry.backoff-ms", "250");
        return m;
    }
}
