package demo;

/**
 * Real, deterministic proof (no live LLM call, no randomness -- every run
 * produces an identical result) that indirect prompt injection via a tool's
 * OWN output is a real, reproducible attack against a naive agent, and that
 * treating tool output as data (never re-parsed for instructions) plus a
 * real, separately-obtained human-approval gate for sensitive actions
 * defeats the identical payload.
 */
public class PromptInjectionDemo {

    public static void main(String[] args) {
        String userQuery = "What is the capital of France?";

        System.out.println("=== 1. UNSAFE agent: re-parses tool output for embedded directives ===");
        SensitiveTools unsafeTools = new SensitiveTools();
        UnsafeAgent unsafeAgent = new UnsafeAgent(new SearchTool(), unsafeTools);
        System.out.println("  User asked: \"" + userQuery + "\"");
        String unsafeResult = unsafeAgent.handleTask(userQuery);
        System.out.println("  " + unsafeResult.replace("\n", "\n  "));
        System.out.println("  Database wiped: " + unsafeTools.wasDatabaseWiped()
                + " (real attempts made: " + unsafeTools.wipeAttempts() + ")");
        System.out.println("  RESULT: the user only asked a geography question. The database was wiped anyway,");
        System.out.println("  by content that arrived embedded in a tool's OWN output -- indirect prompt injection.");

        System.out.println();
        System.out.println("=== 2. SAFE agent: identical poisoned tool output, treated strictly as data ===");
        SensitiveTools safeTools = new SensitiveTools();
        SafeAgent safeAgent = new SafeAgent(new SearchTool(), safeTools);
        System.out.println("  User asked: \"" + userQuery + "\"");
        String safeResult = safeAgent.handleTask(userQuery);
        System.out.println("  " + safeResult);
        System.out.println("  Database wiped: " + safeTools.wasDatabaseWiped()
                + " (real attempts made: " + safeTools.wipeAttempts() + ")");
        System.out.println("  RESULT: the identical injected payload arrived, but was never re-interpreted as an");
        System.out.println("  instruction -- the structural fix, not a filter that could itself be bypassed.");

        System.out.println();
        System.out.println("=== 3. The human-approval gate is real in both directions ===");
        String blockedAttempt = safeAgent.requestSensitiveAction("wipe_database", false);
        System.out.println("  Sensitive action requested WITHOUT real human approval: " + blockedAttempt);
        System.out.println("  Database wiped: " + safeTools.wasDatabaseWiped());
        String approvedAttempt = safeAgent.requestSensitiveAction("wipe_database", true);
        System.out.println("  Sensitive action requested WITH real human approval:    " + approvedAttempt);
        System.out.println("  Database wiped: " + safeTools.wasDatabaseWiped());
        System.out.println("  RESULT: the gate genuinely blocks without approval and genuinely allows with it --");
        System.out.println("  not a decorative check that always fails open or always fails closed.");
    }
}
