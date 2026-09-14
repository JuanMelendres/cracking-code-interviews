package demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Real, executed demo backing
 * syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md
 * (T-2304).
 *
 * No live LLM call. The "agent's" next-action decisions are real,
 * deterministic Java logic standing in for what a real model's tool_use
 * decision would look like at each step -- this demo's job is the real,
 * verifiable LOOP mechanics (multi-step chaining, termination safety,
 * parallel vs. sequential tool execution timing), not a claim about a real
 * model's actual reasoning.
 */
public class AgenticWorkflowDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=== 1. Multi-step tool chain: each step's input depends on the previous step's real output ===");
        multiStepChain();

        System.out.println();
        System.out.println("=== 2. Runaway-loop safety: a real max-iteration cap stops a task that never resolves ===");
        runawayLoopSafety();

        System.out.println();
        System.out.println("=== 3. Sequential vs. parallel tool execution: real, measured wall-clock difference ===");
        sequentialVsParallel();
    }

    // ---- 1. Multi-step chain --------------------------------------------

    private static void multiStepChain() {
        String task = "What's the weather in the capital of France?";
        System.out.println("task: " + task);

        int iteration = 0;
        String capital = null;
        String weather = null;

        while (true) {
            iteration++;
            if (capital == null) {
                System.out.println("iteration " + iteration + ": agent decides -> call getCapital(\"France\")");
                capital = Tools.getCapital("France");
                System.out.println("  real tool result: \"" + capital + "\"");
                continue;
            }
            if (weather == null) {
                System.out.println("iteration " + iteration + ": agent decides -> call getWeather(\"" + capital + "\")  [input depends on the PREVIOUS step's real output]");
                weather = Tools.getWeather(capital);
                System.out.println("  real tool result: \"" + weather + "\"");
                continue;
            }
            System.out.println("iteration " + iteration + ": agent decides -> enough info, produce final answer");
            System.out.println("final answer: The weather in " + capital + " (the capital of France) is " + weather + ".");
            break;
        }
        System.out.println(">>> real total iterations: " + iteration);
    }

    // ---- 2. Runaway-loop safety -----------------------------------------

    private static void runawayLoopSafety() {
        String task = "Give me a definitive status update.";
        System.out.println("task: " + task + "  (deliberately built so the tool never returns a final, usable answer)");

        int maxIterations = 5;
        int iteration = 0;
        boolean resolved = false;

        while (iteration < maxIterations) {
            iteration++;
            String result = Tools.getAmbiguousStatus(task);
            System.out.println("iteration " + iteration + ": agent calls getAmbiguousStatus -> \"" + result + "\" (still not resolved)");
            // real, deterministic logic: this tool's output never satisfies the "resolved" condition
            resolved = false;
        }

        if (!resolved) {
            System.out.println(">>> real max-iteration cap (" + maxIterations + ") reached without resolution -- aborting instead of looping forever.");
            System.out.println(">>> real iterations actually run: " + iteration);
        }
    }

    // ---- 3. Sequential vs. parallel tool execution ----------------------

    private static void sequentialVsParallel() throws Exception {
        long seqStart = System.currentTimeMillis();
        String parisWeather = Tools.getWeather("Paris");
        String tokyoWeather = Tools.getWeather("Tokyo");
        long seqElapsed = System.currentTimeMillis() - seqStart;
        System.out.println("sequential: Paris=" + parisWeather + ", Tokyo=" + tokyoWeather + " -- real elapsed: " + seqElapsed + "ms");

        ExecutorService pool = Executors.newFixedThreadPool(2);
        long parStart = System.currentTimeMillis();
        Future<String> parisFuture = pool.submit(() -> Tools.getWeather("Paris"));
        Future<String> tokyoFuture = pool.submit(() -> Tools.getWeather("Tokyo"));
        String parisResult = parisFuture.get();
        String tokyoResult = tokyoFuture.get();
        long parElapsed = System.currentTimeMillis() - parStart;
        pool.shutdown();
        System.out.println("parallel:   Paris=" + parisResult + ", Tokyo=" + tokyoResult + " -- real elapsed: " + parElapsed + "ms");

        System.out.printf(">>> real, measured speedup: %.1fx (two independent tool calls, no data dependency between them)%n",
                (double) seqElapsed / parElapsed);
    }
}
