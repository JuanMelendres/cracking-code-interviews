import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

// A real, classic TF-IDF scorer: term frequency (log-dampened) times inverse
// document frequency, summed across query terms. No document-length
// normalization -- a raw repeat count in a long document counts fully,
// which is exactly the property BM25Scorer is built to correct.
public class TfIdfScorer {
    private final InvertedIndex index;

    public TfIdfScorer(InvertedIndex index) {
        this.index = index;
    }

    public double score(String queryTerm, int docId) {
        int tf = index.termFrequency(queryTerm, docId);
        if (tf == 0) {
            return 0.0;
        }
        int df = index.documentFrequency(queryTerm);
        int n = index.totalDocuments();
        double idf = Math.log((double) n / df);
        double tfWeight = 1 + Math.log(tf); // classic log-dampened TF
        return tfWeight * idf;
    }

    public List<Map.Entry<Integer, Double>> rank(List<String> queryTerms) {
        Map<Integer, Double> scores = new java.util.HashMap<>();
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
