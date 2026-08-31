import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// A real BM25 scorer -- the ranking function behind Elasticsearch/Lucene's
// default relevance scoring. Two real corrections over plain TF-IDF:
// term-frequency SATURATION (k1 controls how quickly extra repeats of a term
// stop adding score) and document-length NORMALIZATION (b controls how much
// a document longer than average is penalized, since raw term counts are
// naturally higher in longer documents for reasons unrelated to relevance).
public class Bm25Scorer {
    private static final double K1 = 1.5;
    private static final double B = 0.75;

    private final InvertedIndex index;

    public Bm25Scorer(InvertedIndex index) {
        this.index = index;
    }

    public double score(String queryTerm, int docId) {
        int tf = index.termFrequency(queryTerm, docId);
        if (tf == 0) {
            return 0.0;
        }
        int df = index.documentFrequency(queryTerm);
        int n = index.totalDocuments();
        // The BM25 IDF variant (Robertson-Sparck Jones), not the plain log(N/df)
        // used in TfIdfScorer -- this one never goes negative for common terms.
        double idf = Math.log(1 + (n - df + 0.5) / (df + 0.5));

        double docLen = index.docLength(docId);
        double avgDocLen = index.averageDocLength();
        double normalizedTf = (tf * (K1 + 1)) / (tf + K1 * (1 - B + B * (docLen / avgDocLen)));

        return idf * normalizedTf;
    }

    public List<Map.Entry<Integer, Double>> rank(List<String> queryTerms) {
        Map<Integer, Double> scores = new HashMap<>();
        for (String term : queryTerms) {
            for (Integer docId : index.postingsFor(term).keySet()) {
                scores.merge(docId, score(term, docId), Double::sum);
            }
        }
        List<Map.Entry<Integer, Double>> ranked = new ArrayList<>(scores.entrySet());
        ranked.sort(Comparator.<Map.Entry<Integer, Double>>comparingDouble(Map.Entry::getValue).reversed());
        return ranked;
    }
}
