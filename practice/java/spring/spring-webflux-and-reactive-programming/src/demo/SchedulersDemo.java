package demo;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class SchedulersDemo {

    public static void main(String[] args) {
        System.out.println("Main thread: " + Thread.currentThread().getName());

        System.out.println();
        System.out.println("=== subscribeOn: moves the WHOLE chain, including the source ===");
        Mono.fromSupplier(() -> {
            System.out.println("source runs on: " + Thread.currentThread().getName());
            return "value";
        })
        .subscribeOn(Schedulers.boundedElastic())
        .doOnNext(v -> System.out.println("doOnNext observes: " + Thread.currentThread().getName()))
        .block();

        System.out.println();
        System.out.println("=== publishOn: only moves operators AFTER it -- but ONLY if the source can't be fused ===");
        System.out.println("--- without .hide(): Reactor fuses this simple source into publishOn's own pull loop ---");
        Mono.fromSupplier(() -> {
            System.out.println("source runs on: " + Thread.currentThread().getName());
            return "value";
        })
        .publishOn(Schedulers.boundedElastic())
        .doOnNext(v -> System.out.println("doOnNext observes: " + Thread.currentThread().getName()))
        .block();
        System.out.println("(real result: the source ALSO ran on boundedElastic -- fusion pulled it there)");

        System.out.println();
        System.out.println("--- with .hide(): fusion is disabled, restoring the textbook push-based distinction ---");
        Mono.fromSupplier(() -> {
            System.out.println("source runs on: " + Thread.currentThread().getName());
            return "value";
        })
        .hide()
        .publishOn(Schedulers.boundedElastic())
        .doOnNext(v -> System.out.println("doOnNext observes: " + Thread.currentThread().getName()))
        .block();
        System.out.println("(real result: the source stayed on main -- only doOnNext moved)");
    }
}
