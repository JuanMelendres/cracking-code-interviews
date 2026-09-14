// F-214: a real, separate Spring Boot backend (its own JVM process, its own
// port) that the Next.js app in practice/frontend/react-nextjs-fundamentals/
// integrates with -- the exact "API is a separate service" scenario the
// register names. Deliberately in package `demo`, not the default package --
// see EmbeddedServerDemo.java in the sibling spring-vs-spring-boot chapter
// for the real, documented reason (a real NoClassDefFoundError otherwise).
package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class,
            "--server.port=8080",
            "--logging.level.root=WARN"
        );
    }
}
