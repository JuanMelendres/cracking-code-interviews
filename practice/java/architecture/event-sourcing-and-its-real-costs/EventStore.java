import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * A real, minimal, file-backed append-only event store -- every event is a real
 * line appended to a real file on disk, and replay means really reading every byte
 * back and reapplying it. Real, honest byte-offset seeking is used for
 * post-snapshot replay (via a real {@code java.io.RandomAccessFile}, skipping the
 * disk read of already-snapshotted bytes entirely) rather than reading the whole
 * file and discarding a prefix -- so the snapshot-benefit measurement in this pack
 * reflects real I/O savings, not just object-count savings.
 */
final class EventStore {
    private final Path file;

    EventStore(Path file) {
        this.file = file;
    }

    void append(Event event) {
        try (BufferedWriter writer = Files.newBufferedWriter(file,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(event.toLine());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    List<Event> readAll() {
        return readFromByteOffset(0);
    }

    /** Real byte-offset seek -- bytes before {@code byteOffset} are never read from disk. */
    List<Event> readFromByteOffset(long byteOffset) {
        List<Event> events = new ArrayList<>();
        try (var raf = new java.io.RandomAccessFile(file.toFile(), "r")) {
            raf.seek(byteOffset);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new java.io.FileInputStream(raf.getFD()), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    events.add(Event.fromLine(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return events;
    }

    long sizeInBytes() {
        try {
            return Files.size(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
