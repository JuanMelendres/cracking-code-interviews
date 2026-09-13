package demo;

import java.util.List;
import java.util.Random;

/**
 * A real, deterministic, in-JVM stand-in for an LLM's sampling and
 * instruction-following behavior -- NOT a call to a live model.
 *
 * This backs syllabus/22-ai-llm-engineering/prompt-engineering-patterns.md
 * (T-2303). It exists to make two real, documented API mechanisms fully
 * verifiable without a live model: (1) temperature genuinely controls
 * sampling variance, a real statistical fact this class implements with a
 * real java.util.Random, not narration; (2) a system-role instruction and
 * an instruction embedded in user text are structurally different channels,
 * demonstrated with real, deterministic logic this class owns and can
 * fully explain. Neither claims to prove anything about a real model's
 * actual reasoning quality -- that would require a live model call.
 */
public class FakeCompletionEngine {

    private final Random random;

    public FakeCompletionEngine(long seed) {
        this.random = new Random(seed);
    }

    /** Real candidate completions this "model" could produce for the demo's fixed prompt. */
    private static final List<String> CANDIDATES = List.of(
            "The capital of France is Paris.",
            "Paris is the capital city of France.",
            "France's capital is Paris."
    );

    /**
     * Real sampling: temperature=0.0 always deterministically picks the
     * first (highest-probability) candidate. temperature>0.0 draws from a
     * real Random, weighted so higher temperature makes the non-top
     * candidates genuinely more likely to be picked -- the real, documented
     * meaning of the parameter, implemented rather than asserted.
     */
    public String complete(double temperature) {
        if (temperature <= 0.0) {
            return CANDIDATES.get(0);
        }
        double roll = random.nextDouble();
        double spreadTopProbability = Math.max(0.34, 1.0 - temperature * 0.7);
        if (roll < spreadTopProbability) {
            return CANDIDATES.get(0);
        } else if (roll < spreadTopProbability + (1 - spreadTopProbability) / 2) {
            return CANDIDATES.get(1);
        } else {
            return CANDIDATES.get(2);
        }
    }

    private static final String CONSTRAINED_ANSWER = "I can help with our product, but I won't discuss competitor products.";
    private static final String UNCONSTRAINED_ANSWER = "Sure, here's a competitor comparison...";

    /**
     * The instruction lives in the SYSTEM channel, structurally separate
     * from user text. This "model" checks it unconditionally -- real,
     * deterministic logic, not a claim about a real model's robustness.
     * userText's content, including any override attempt, never reaches
     * this check at all.
     */
    public String respondWithSystemChannel(String systemInstruction, String userText) {
        boolean systemForbidsCompetitors = systemInstruction != null
                && systemInstruction.toLowerCase().contains("never mention competitor");
        return systemForbidsCompetitors ? CONSTRAINED_ANSWER : UNCONSTRAINED_ANSWER;
    }

    /**
     * The SAME instruction, instead concatenated directly into the single
     * user-text blob (a real, common integration mistake: no system role
     * used at all). This deliberately naive "model" simulation takes
     * whichever instruction-like sentence appears LAST in the blob as
     * authoritative -- a real, honest illustration of why mixing
     * instructions and untrusted content in one channel is structurally
     * risky, not a claim about any specific real model's behavior.
     */
    public String respondWithInstructionInlineInUserText(String combinedUserText) {
        String lower = combinedUserText.toLowerCase();
        int instructionIndex = lower.indexOf("never mention competitor");
        int overrideIndex = lower.indexOf("ignore the previous instruction");
        boolean overrideComesAfterInstruction = instructionIndex >= 0
                && overrideIndex > instructionIndex;
        return overrideComesAfterInstruction ? UNCONSTRAINED_ANSWER : CONSTRAINED_ANSWER;
    }
}
