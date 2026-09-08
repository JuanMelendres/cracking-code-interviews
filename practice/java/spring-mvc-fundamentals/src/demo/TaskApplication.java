package demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// T-2203: a minimal, real Spring MVC application demonstrating
// Controller -> Service -> Repository, all wired by constructor-based
// dependency injection -- no field @Autowired, no XML, no Maven/Gradle.
@SpringBootApplication
public class TaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskApplication.class,
            "--server.port=8080",
            "--logging.level.root=WARN"
        );
    }
}
