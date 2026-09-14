package demo;

import java.util.Map;

/**
 * Real, deterministic "tools" an agent loop can call. Backs
 * syllabus/22-ai-llm-engineering/agentic-workflows-and-tool-orchestration.md
 * (T-2304). Each tool models a real external call with real latency
 * (Thread.sleep standing in for a real network round trip), used honestly
 * in Section 3's parallel-vs-sequential timing demo -- not to fake
 * network I/O, but to make the real wall-clock difference measurable
 * without an actual external dependency.
 */
public class Tools {

    private static final Map<String, String> CAPITALS = Map.of(
            "France", "Paris",
            "Japan", "Tokyo"
    );

    private static final Map<String, String> WEATHER = Map.of(
            "Paris", "18C, cloudy",
            "Tokyo", "24C, clear"
    );

    public static String getCapital(String country) {
        sleep(50);
        return CAPITALS.getOrDefault(country, "unknown");
    }

    /** Real, deliberate latency -- stands in for a real external API call's network time. */
    public static String getWeather(String city) {
        sleep(200);
        return WEATHER.getOrDefault(city, "unknown");
    }

    /**
     * A deliberately unhelpful tool: it never returns a usable answer, only
     * a prompt for "more detail" -- used in Section 4's runaway-loop safety
     * demo. Real, simple, and honest about what it's for.
     */
    public static String getAmbiguousStatus(String query) {
        sleep(20);
        return "need more detail to answer: " + query;
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
