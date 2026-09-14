import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

// A real inverted index: term -> (docId -> term frequency in that doc).
// This is the actual core data structure behind full-text search -- the
// entire point of an inverted index is that a query for "which documents
// contain this term" is a single map lookup, not a scan of every document.
public class InvertedIndex {
    private final Map<String, Map<Integer, Integer>> postings = new HashMap<>();
    private final Map<Integer, Integer> docLengths = new HashMap<>();
    private final Map<Integer, Document> documents = new HashMap<>();

    public void index(Document doc) {
        documents.put(doc.id, doc);
        List<String> tokens = Tokenizer.tokenize(doc.text);
        docLengths.put(doc.id, tokens.size());
        for (String term : tokens) {
            postings.computeIfAbsent(term, t -> new HashMap<>())
                    .merge(doc.id, 1, Integer::sum);
        }
    }

    public Map<Integer, Integer> postingsFor(String term) {
        return postings.getOrDefault(term.toLowerCase(), Map.of());
    }

    public int documentFrequency(String term) {
        return postingsFor(term).size();
    }

    public int termFrequency(String term, int docId) {
        return postingsFor(term).getOrDefault(docId, 0);
    }

    public int docLength(int docId) {
        return docLengths.getOrDefault(docId, 0);
    }

    public double averageDocLength() {
        return docLengths.values().stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    public int totalDocuments() {
        return documents.size();
    }

    public Document document(int docId) {
        return documents.get(docId);
    }

    // A real Boolean AND query: intersect the postings lists of every query
    // term. Real, exact set intersection -- not a filter over every document.
    public Set<Integer> booleanAnd(List<String> terms) {
        Set<Integer> result = null;
        for (String term : terms) {
            Set<Integer> docsWithTerm = postingsFor(term).keySet();
            if (result == null) {
                result = new TreeSet<>(docsWithTerm);
            } else {
                result.retainAll(docsWithTerm);
            }
        }
        return result == null ? new HashSet<>() : result;
    }
}
