import java.util.List;
import java.util.Map;
import java.util.Set;

public class InvertedIndexDemo {

    public static void main(String[] args) {
        InvertedIndex index = SampleCorpus.build();

        System.out.println("=== Real postings list for the term \"java\" ===");
        Map<Integer, Integer> postings = index.postingsFor("java");
        postings.forEach((docId, tf) ->
                System.out.println("doc " + docId + " (\"" + index.document(docId).title + "\"): tf=" + tf));

        System.out.println();
        System.out.println("=== Real postings list for the term \"database\" ===");
        index.postingsFor("database").forEach((docId, tf) ->
                System.out.println("doc " + docId + " (\"" + index.document(docId).title + "\"): tf=" + tf));

        System.out.println();
        System.out.println("=== Real Boolean AND query: \"java\" AND \"garbage\" AND \"collection\" ===");
        Set<Integer> matches = index.booleanAnd(List.of("java", "garbage", "collection"));
        System.out.println("Real matching doc IDs: " + matches);
        for (Integer docId : matches) {
            System.out.println("  doc " + docId + ": \"" + index.document(docId).title + "\"");
        }
    }
}
