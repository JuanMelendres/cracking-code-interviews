package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

// Runs the identical application as EmbeddedServerDemo, but with --debug, which
// makes Spring Boot print its real, evaluated auto-configuration report: every
// candidate auto-configuration class it considered, and precisely why each one
// was applied (a "positive match" -- its @Conditional evaluated true, e.g. the
// right class was found on the classpath) or skipped (a "negative match" --
// e.g. no DataSource class present, so DataSourceAutoConfiguration didn't apply).
public class AutoConfigReportDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("== Same application, started with --debug: Spring Boot's real, evaluated auto-configuration report ==");
        System.out.println();

        ConfigurableApplicationContext ctx = SpringApplication.run(EmbeddedServerDemo.class,
            "--server.port=0",
            "--debug",
            "--logging.level.root=OFF" // suppress ordinary logging noise; the debug report prints via System.out directly
        );
        ctx.close();
    }
}
