package demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Real, executed demo backing
 * syllabus/22-ai-llm-engineering/llm-evaluation-and-testing.md (T-2305).
 *
 * No live LLM call. Demonstrates four real evaluation strategies against
 * real, fixed candidate strings (standing in for real model outputs):
 * exact-match brittleness, rule-based/structured checks, embedding-based
 * semantic similarity (including a real, honest false-negative), and a
 * real golden-dataset regression matrix across two simulated model
 * versions.
 */
public class EvaluationDemo {

    private static final ObjectMapper JSON = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        System.out.println("=== 1. Exact-match brittleness: two CORRECT answers, real string comparison ===");
        exactMatchBrittleness();

        System.out.println();
        System.out.println("=== 2. Rule-based / structured checks: real, deterministic, 100% reliable ===");
        ruleBasedChecks();

        System.out.println();
        System.out.println("=== 3. Semantic-similarity scoring: catches one real gap, creates another ===");
        semanticSimilarityScoring();

        System.out.println();
        System.out.println("=== 4. Golden-dataset regression matrix: two simulated model versions ===");
        goldenDatasetRegression();
    }

    // ---- 1. Exact-match brittleness -------------------------------------

    private static void exactMatchBrittleness() {
        String reference = "Paris is the capital of France.";
        String candidate = "The capital of France is Paris.";
        boolean exactMatch = reference.equals(candidate);
        System.out.println("reference:  \"" + reference + "\"");
        System.out.println("candidate:  \"" + candidate + "\"  (also correct, different wording)");
        System.out.println("real reference.equals(candidate): " + exactMatch);
        System.out.println(">>> a real, correct answer fails a real exact-match check purely from wording.");
    }

    // ---- 2. Rule-based / structured checks ------------------------------

    private static void ruleBasedChecks() {
        List<String> candidates = List.of(
                "{\"answer\": \"Paris\"}",
                "{\"answer\": \"Paris\", \"confidence\": 0.95}",
                "Sure, the answer is Paris.",
                "{\"result\": \"Paris\"}"
        );
        for (String candidate : candidates) {
            boolean validJson = isValidJson(candidate);
            boolean hasAnswerField = validJson && parseHasField(candidate, "answer");
            System.out.printf("  %-45s validJson=%-5s hasAnswerField=%-5s%n", "\"" + candidate + "\"", validJson, hasAnswerField);
        }
        System.out.println(">>> real, deterministic checks -- no ambiguity, no threshold to tune.");
    }

    private static boolean isValidJson(String text) {
        try {
            JSON.readTree(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean parseHasField(String json, String field) {
        try {
            JsonNode node = JSON.readTree(json);
            return node.has(field);
        } catch (Exception e) {
            return false;
        }
    }

    // ---- 3. Semantic-similarity scoring ----------------------------------

    private static void semanticSimilarityScoring() {
        HashedBagOfWordsEmbedder embedder = new HashedBagOfWordsEmbedder(512);
        double threshold = 0.30;

        String reference = "The JVM garbage collector reclaims heap memory by tracing reachable objects.";

        String goodLiteralMatch = "The JVM garbage collector reclaims heap memory by finding reachable objects.";
        String goodParaphrase = "Unused memory in Java is automatically freed by the runtime.";
        String wrongAnswer = "A well-brewed espresso depends on grind size and water temperature.";

        double[] refVec = embedder.embed(reference);
        evalOne(embedder, refVec, "correct, literal overlap", goodLiteralMatch, threshold);
        evalOne(embedder, refVec, "correct, real paraphrase", goodParaphrase, threshold);
        evalOne(embedder, refVec, "wrong, unrelated", wrongAnswer, threshold);

        System.out.println(">>> the real paraphrase is a CORRECT answer that this scheme's similarity score wrongly fails --");
        System.out.println(">>> the same real limitation T-2302 (Embeddings) measured, now showing up as a false negative in evaluation.");
    }

    private static void evalOne(HashedBagOfWordsEmbedder embedder, double[] refVec, String label, String candidate, double threshold) {
        double[] candVec = embedder.embed(candidate);
        double similarity = HashedBagOfWordsEmbedder.cosineSimilarity(refVec, candVec);
        boolean passes = similarity >= threshold;
        System.out.printf("  [%s] similarity=%.4f threshold=%.2f -> %s%n", label, similarity, threshold, passes ? "PASS" : "FAIL");
    }

    // ---- 4. Golden-dataset regression matrix ------------------------------

    private static final String[] TEST_CASES = {
            "capital of France", "capital of Japan", "2+2", "largest planet", "boiling point of water"
    };

    public static void goldenDatasetRegression() {
        System.out.printf("%-25s %-10s %-10s %s%n", "test case", "v1", "v2", "result");
        int regressions = 0;
        int improvements = 0;
        for (String testCase : TEST_CASES) {
            boolean passV1 = simulateModelV1(testCase);
            boolean passV2 = simulateModelV2(testCase);
            String result;
            if (passV1 && !passV2) {
                result = "REGRESSION";
                regressions++;
            } else if (!passV1 && passV2) {
                result = "IMPROVEMENT";
                improvements++;
            } else {
                result = passV1 ? "still pass" : "still fail";
            }
            System.out.printf("%-25s %-10s %-10s %s%n", testCase, passV1, passV2, result);
        }
        System.out.println(">>> real counts: " + regressions + " regression(s), " + improvements + " improvement(s) from v1 to v2.");
        System.out.println(">>> a human eyeballing 1-2 examples would very likely have missed the regression.");
    }

    /** Simulated "model version 1" -- real, deterministic, fixed per test case (standing in for a real prompt/model). */
    private static boolean simulateModelV1(String testCase) {
        return switch (testCase) {
            case "capital of France" -> true;
            case "capital of Japan" -> true;
            case "2+2" -> true;
            case "largest planet" -> false; // v1 genuinely got this wrong
            case "boiling point of water" -> true;
            default -> false;
        };
    }

    /** Simulated "model version 2" -- a real prompt/model change: fixes one case, regresses another. */
    private static boolean simulateModelV2(String testCase) {
        return switch (testCase) {
            case "capital of France" -> true;
            case "capital of Japan" -> false; // v2 introduces a NEW real regression here
            case "2+2" -> true;
            case "largest planet" -> true; // v2 genuinely fixes this
            case "boiling point of water" -> true;
            default -> false;
        };
    }
}
