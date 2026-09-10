package demo;

/**
 * Real, executed demo backing
 * syllabus/22-ai-llm-engineering/embeddings.md (T-2302).
 *
 * Computes real cosine similarity, via a real hashed bag-of-words
 * embedder, across four sentence-pair categories chosen to expose exactly
 * where this kind of naive, non-trained embedding scheme succeeds and
 * fails -- and how much a wider dimension count helps (or doesn't).
 *
 * This demo does NOT call a live embedding-API model -- stated explicitly.
 * Its purpose is to make the real MATH (dot product on L2-normalized
 * vectors, hash-collision behavior at different dimension counts) fully
 * verifiable, not to claim semantic quality a hashing scheme cannot have.
 */
public class EmbeddingsDemo {

    private static final String[][] PAIRS = {
        {
            "TRUE POSITIVE (shared vocabulary, same meaning)",
            "The JVM garbage collector reclaims heap memory by tracing reachable objects.",
            "Garbage collection in the JVM frees heap memory by finding reachable objects."
        },
        {
            "PARAPHRASE (same meaning, different words)",
            "The JVM garbage collector reclaims heap memory by tracing reachable objects.",
            "Unused memory in a Java program is automatically freed by the runtime."
        },
        {
            "POLYSEMY (shared word, different meaning)",
            "I need to check my bank account balance before paying rent.",
            "We sat by the river bank and watched the water flow past."
        },
        {
            "TRUE NEGATIVE (unrelated, no shared vocabulary)",
            "The JVM garbage collector reclaims heap memory by tracing reachable objects.",
            "A well-brewed espresso depends on grind size and water temperature."
        }
    };

    public static void main(String[] args) {
        for (int dimensions : new int[]{32, 512}) {
            System.out.println("=== dimensions = " + dimensions + " ===");
            HashedBagOfWordsEmbedder embedder = new HashedBagOfWordsEmbedder(dimensions);
            for (String[] pair : PAIRS) {
                double[] a = embedder.embed(pair[1]);
                double[] b = embedder.embed(pair[2]);
                double similarity = HashedBagOfWordsEmbedder.cosineSimilarity(a, b);
                System.out.printf("%-55s cosine similarity = %.4f%n", pair[0], similarity);
            }
            System.out.println();
        }
    }
}
