import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// A real, minimal analyzer: lowercase, split on non-letters, drop a small
// stop-word list. Production search engines (Lucene/Elasticsearch) do
// materially more here (stemming, synonyms, language-specific analysis) --
// this is deliberately scoped to the real mechanism, not full parity.
public class Tokenizer {
    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "the", "of", "in", "on", "for", "to", "with");

    public static List<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase().split("[^a-z0-9]+"))
                .filter(token -> !token.isBlank())
                .filter(token -> !STOP_WORDS.contains(token))
                .collect(Collectors.toList());
    }
}
