package demo;

import java.util.Locale;

/**
 * Same real, deterministic hashing-trick embedder used in
 * practice/java/embeddings-fundamentals/ and rag-and-vector-databases/,
 * reproduced here (small, self-contained, per this repo's practice-pack
 * convention) for this chapter's semantic-similarity scoring demo. NOT a
 * trained model -- see T-2302 (Embeddings) for the real limitations this
 * scheme has, which this chapter's Section 4 deliberately re-exercises in
 * an evaluation context.
 */
public class HashedBagOfWordsEmbedder {

    private final int dimensions;

    public HashedBagOfWordsEmbedder(int dimensions) {
        this.dimensions = dimensions;
    }

    public double[] embed(String text) {
        double[] vector = new double[dimensions];
        String[] words = text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9 ]", " ").split("\\s+");
        for (String word : words) {
            if (word.isBlank()) continue;
            int bucket = Math.floorMod(word.hashCode(), dimensions);
            vector[bucket] += 1.0;
        }
        return l2Normalize(vector);
    }

    private double[] l2Normalize(double[] v) {
        double sumSquares = 0.0;
        for (double x : v) sumSquares += x * x;
        double norm = Math.sqrt(sumSquares);
        if (norm == 0.0) return v;
        double[] out = new double[v.length];
        for (int i = 0; i < v.length; i++) out[i] = v[i] / norm;
        return out;
    }

    public static double cosineSimilarity(double[] a, double[] b) {
        double dot = 0.0;
        for (int i = 0; i < a.length; i++) dot += a[i] * b[i];
        return dot;
    }
}
