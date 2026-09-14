package demo;

import java.util.Locale;

/**
 * A real, deterministic, explainable embedding function -- NOT a trained
 * neural embedding model. Uses the real "hashing trick" (feature hashing):
 * each word hashes into one of N fixed dimensions, term counts accumulate
 * there, and the result is L2-normalized.
 *
 * Same technique as practice/java/rag-and-vector-databases/'s embedder,
 * reproduced here (small, self-contained per this repo's practice-pack
 * convention) because this chapter (T-2302, Embeddings) needs to vary its
 * dimension count, which that pack's fixed-dimension usage doesn't exercise.
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

    /** Both inputs are already L2-normalized, so cosine similarity is just the dot product. */
    public static double cosineSimilarity(double[] a, double[] b) {
        double dot = 0.0;
        for (int i = 0; i < a.length; i++) dot += a[i] * b[i];
        return dot;
    }
}
