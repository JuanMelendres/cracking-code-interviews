import java.util.List;
import java.util.Map;

// Runs the identical query against both a real, classic TF-IDF scorer and a
// real BM25 scorer over the identical corpus, to surface a real, measured
// ranking difference caused by BM25's term-frequency saturation and
// document-length normalization.
public class ScoringComparisonDemo {

    public static void main(String[] args) {
        InvertedIndex index = SampleCorpus.build();
        List<String> query = List.of("java", "garbage", "collection", "tuning");

        System.out.println("Query: " + query);
        System.out.println("Average real document length across the corpus: " + index.averageDocLength() + " tokens");
        System.out.println();

        TfIdfScorer tfIdf = new TfIdfScorer(index);
        Bm25Scorer bm25 = new Bm25Scorer(index);

        System.out.println("=== Real TF-IDF ranking (no length normalization) ===");
        for (Map.Entry<Integer, Double> entry : tfIdf.rank(query)) {
            printResult(index, entry);
        }

        System.out.println();
        System.out.println("=== Real BM25 ranking (length-normalized, TF-saturated) ===");
        for (Map.Entry<Integer, Double> entry : bm25.rank(query)) {
            printResult(index, entry);
        }
    }

    private static void printResult(InvertedIndex index, Map.Entry<Integer, Double> entry) {
        int docId = entry.getKey();
        Document doc = index.document(docId);
        System.out.printf("  doc %d  score=%.4f  length=%d  \"%s\"%n",
                docId, entry.getValue(), index.docLength(docId), doc.title);
    }
}
