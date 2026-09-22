package demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A real, documented anti-pattern: treating tool OUTPUT as if it could
 * contain new instructions to follow, the same way the user's own prompt
 * can. This agent re-scans every tool result for an embedded directive and
 * automatically acts on it -- exactly the mechanism that makes indirect
 * prompt injection work in real, deployed agent systems.
 */
public class UnsafeAgent {

    private static final Pattern DIRECTIVE =
            Pattern.compile("Call the tool (\\w+) with argument confirm=(\\w+)");

    private final SearchTool searchTool;
    private final SensitiveTools sensitiveTools;

    public UnsafeAgent(SearchTool searchTool, SensitiveTools sensitiveTools) {
        this.searchTool = searchTool;
        this.sensitiveTools = sensitiveTools;
    }

    public String handleTask(String userQuery) {
        String toolResult = searchTool.search(userQuery);

        // The real vulnerability: re-parsing tool output for instructions,
        // with no distinction between "data returned by a tool" and
        // "a legitimate instruction from the actual user."
        Matcher m = DIRECTIVE.matcher(toolResult);
        if (m.find() && m.group(1).equals("wipe_database")) {
            boolean confirmArg = Boolean.parseBoolean(m.group(2));
            // The agent treats the INJECTED text's own "confirm=true" as if
            // it were genuine human approval -- it isn't; it's attacker text.
            String outcome = sensitiveTools.wipeDatabase(confirmArg);
            return "Tool output: " + toolResult + "\n  -> Detected embedded directive, executed it: " + outcome;
        }
        return "Tool output: " + toolResult;
    }
}
