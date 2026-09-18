package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** A real, running Spring Boot app (embedded Tomcat, no external server) --
 * springdoc-openapi is on the classpath and needs zero manual wiring: it
 * auto-registers via Spring Boot's own auto-configuration import mechanism
 * and starts serving a real, generated spec at {@code /v3/api-docs} the
 * moment this app starts. */
@SpringBootApplication
public class OpenApiDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpenApiDemoApplication.class, "--server.port=8099");
    }
}
