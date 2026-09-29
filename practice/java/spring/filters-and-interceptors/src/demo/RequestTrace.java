package demo;

import java.util.ArrayList;
import java.util.List;

/**
 * Records, in real execution order, every stage a single request passes through.
 *
 * <p>Deliberately a {@link ThreadLocal}: in the classic (non-async) Spring MVC
 * model every stage below — outer filter, inner filter, both interceptors, the
 * handler method, and the exception resolver — runs on the SAME container
 * thread. The demo prints that thread's name with every step so the claim is
 * visible in the transcript rather than merely asserted.
 */
final class RequestTrace {

    private static final ThreadLocal<List<String>> STEPS = ThreadLocal.withInitial(ArrayList::new);

    private RequestTrace() {
    }

    static void start() {
        STEPS.get().clear();
    }

    static void record(String stage, String detail) {
        List<String> steps = STEPS.get();
        steps.add(String.format("%2d. %-34s | thread=%-22s | %s",
                steps.size() + 1, stage, Thread.currentThread().getName(), detail));
    }

    static void printAndClear(String requestLabel) {
        List<String> steps = STEPS.get();
        System.out.println();
        System.out.println("=== " + requestLabel + " ===");
        steps.forEach(System.out::println);
        System.out.flush();
        STEPS.remove();
    }
}
