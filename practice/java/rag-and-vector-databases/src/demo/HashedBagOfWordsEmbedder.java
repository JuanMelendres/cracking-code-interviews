package demo;

import java.util.Locale;

/**
 * A real, deterministic, explainable embedding function -- NOT a trained
 * neural embedding model. It uses the real "hashing trick" (feature
 * hashing): each word hashes into one of N fixed dimensions, term counts
 * accumulate there, and the resulting vector is L2-normalized.
 *
 * This exists so this chapter's pgvector demo can be 100% real and
 * reproducible without a live embedding-API call. A real production RAG
 * system uses a real trained embedding model (e.g. an OpenAI/Voyage/Cohere
 * embeddings endpoint, or a local sentence-transformer) -- this scheme is
 * intentionally simpler, so the pgvector storage/distance/indexing mechanics
 * below can be verified with real, deterministic math, not so it can stand
 * in for real semantic embedding quality. Said explicitly in the chapter
 * this backs, not just here.
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

    /** pgvector literal format: '[0.1,0.2,...]' */
    public String toVectorLiteral(double[] v) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < v.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(v[i]);
        }
        return sb.append("]").toString();
    }
}
