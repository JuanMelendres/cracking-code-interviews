package demo;

/**
 * The real, structural fix: tool output is ALWAYS treated as data, never
 * re-parsed for embedded instructions, no matter what it contains. Separately,
 * any sensitive tool call requires human approval obtained through a real,
 * separate channel the agent itself cannot forge -- never inferred from
 * content the agent received, injected or not.
 */
public class SafeAgent {

    private final SearchTool searchTool;
    private final SensitiveTools sensitiveTools;

    public SafeAgent(SearchTool searchTool, SensitiveTools sensitiveTools) {
        this.searchTool = searchTool;
        this.sensitiveTools = sensitiveTools;
    }

    public String handleTask(String userQuery) {
        String toolResult = searchTool.search(userQuery);
        // Tool output is data. Full stop. It is never scanned for directives,
        // never re-fed as if it were an instruction stream. Whatever text it
        // contains, this agent only ever answers the user's original query.
        return "Tool output (treated as pure data): " + toolResult;
    }

    /**
     * The only path to a sensitive action -- requires a real, separately
     * obtained approval flag, never derived from tool output content.
     */
    public String requestSensitiveAction(String toolName, boolean realHumanApproval) {
        if (toolName.equals("wipe_database")) {
            return sensitiveTools.wipeDatabase(realHumanApproval);
        }
        return "Unknown sensitive action: " + toolName;
    }
}
