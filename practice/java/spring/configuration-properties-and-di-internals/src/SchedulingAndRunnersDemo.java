import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Real proof of two independent things:
 *  1. @Scheduled runs a method repeatedly, on a background scheduler thread,
 *     entirely independent of any request or caller -- verified by a real
 *     counter incrementing while the main thread does nothing but sleep.
 *  2. A CommandLineRunner bean runs automatically, driven by SpringApplication
 *     itself (a plain AnnotationConfigApplicationContext does NOT do this --
 *     it's specifically SpringApplication.run()'s job), after the context is
 *     fully refreshed but before SpringApplication.run() returns to the
 *     caller.
 */
public class SchedulingAndRunnersDemo {

    static class Ticker {
        final AtomicInteger ticks = new AtomicInteger();

        @Scheduled(fixedRate = 100)
        void tick() {
            ticks.incrementAndGet();
        }
    }

    @Configuration
    @EnableScheduling
    static class SchedulingConfig {
        @Bean Ticker ticker() { return new Ticker(); }
    }

    static final AtomicBoolean RUNNER_EXECUTED = new AtomicBoolean(false);

    @Configuration
    static class RunnerConfig {
        @Bean
        CommandLineRunner startupRunner() {
            return args -> {
                RUNNER_EXECUTED.set(true);
                System.out.println("CommandLineRunner executing -- every other bean already exists at this point.");
            };
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("== @Scheduled: real, repeated background execution ==");
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SchedulingConfig.class)) {
            Ticker ticker = ctx.getBean(Ticker.class);
            System.out.println("Immediately after context refresh, ticks so far: " + ticker.ticks.get());
            Thread.sleep(550); // main thread does NOTHING else -- ticks below prove a background thread is doing the work
            System.out.println("After sleeping 550ms with the main thread idle, ticks = " + ticker.ticks.get()
                    + "  (fixedRate=100ms -- a background scheduler thread, not the main thread, drove this)");
        }

        System.out.println();
        System.out.println("== CommandLineRunner under a PLAIN AnnotationConfigApplicationContext: never invoked ==");
        RUNNER_EXECUTED.set(false);
        try (AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RunnerConfig.class)) {
            System.out.println("Context fully refreshed. Has the CommandLineRunner bean executed? " + RUNNER_EXECUTED.get()
                    + "  (the bean EXISTS, but nothing ever calls .run() on it -- that mechanism lives in SpringApplication, not the core container)");
        }

        System.out.println();
        System.out.println("== The IDENTICAL bean, under SpringApplication.run(): actually invoked ==");
        RUNNER_EXECUTED.set(false);
        try (ConfigurableApplicationContext ctx = SpringApplication.run(RunnerConfig.class)) {
            System.out.println("SpringApplication.run() has returned. Has the runner executed? " + RUNNER_EXECUTED.get());
        }
    }
}
