import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RleTest {
    @Test
    void emptyStringEncodesToEmptyString() {
        assertEquals("", Rle.encode(""));
    }

    @Test
    void singleCharacterEncodesWithCountOne() {
        assertEquals("a1", Rle.encode("a"));
    }

    @Test
    void repeatedCharactersEncodeAsRuns() {
        assertEquals("a3b2c1", Rle.encode("aaabbc"));
    }
}
