package demo;

import java.util.Random;

/**
 * Real, deterministic stand-in for the real difference between asking a
 * model to "answer in JSON" via plain prompt text (freeform) versus using a
 * real API's structured-output / JSON-mode parameter (response_format).
 * NOT a live model call. Backs T-2303's Section 7 structured-output demo.
 */
public class FakeFreeformOrJsonEngine {

    private final Random random;

    public FakeFreeformOrJsonEngine(long seed) {
        this.random = new Random(seed);
    }

    /**
     * Simulates a model asked, via plain prompt wording only, to "respond
     * in JSON" -- real APIs document that this is NOT guaranteed valid
     * JSON without the dedicated response_format parameter. Some real
     * fraction of the time, this "model" adds real, genuine surrounding
     * prose that breaks a naive JSON.parse.
     */
    public String answerFreeform() {
        boolean addsSurroundingProse = random.nextDouble() < 0.4;
        if (addsSurroundingProse) {
            return "Sure! Here's the answer in JSON format:\n{\"answer\": \"Paris\"}\nLet me know if you need anything else.";
        }
        return "{\"answer\": \"Paris\"}";
    }

    /** Simulates a real response_format=json_object guarantee: always valid, parseable JSON. */
    public String answerAsJson() {
        return "{\"answer\": \"Paris\"}";
    }
}
