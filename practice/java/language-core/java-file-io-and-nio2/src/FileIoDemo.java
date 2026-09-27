import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Real, executed evidence for the Java File I/O and NIO.2 chapter.
 * Every number printed here is measured on this machine, not asserted from documentation.
 */
public class FileIoDemo {

    public static void main(String[] args) throws Exception {
        Path tmpDir = Files.createTempDirectory("file-io-demo");
        System.out.println("Working directory: " + tmpDir);
        System.out.println();

        demoBufferedVsUnbuffered(tmpDir);
        System.out.println();
        demoCharsetMismatch(tmpDir);
        System.out.println();
        demoSuppressedExceptionOnClose();
        System.out.println();
        demoNio2Basics(tmpDir);
    }

    /** Buffered vs. unbuffered reads: a real, measured syscall-overhead difference. */
    private static void demoBufferedVsUnbuffered(Path dir) throws IOException {
        Path file = dir.resolve("lines.txt");
        int lineCount = 200_000;
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            for (int i = 0; i < lineCount; i++) {
                w.write("line-" + i);
                w.newLine();
            }
        }
        long fileSize = Files.size(file);
        System.out.printf("Demo A: Buffered vs. unbuffered reads (%,d lines, %,d bytes)%n", lineCount, fileSize);

        // Unbuffered: one read() call (and, underneath, one syscall) per character.
        long startUnbuffered = System.nanoTime();
        int charCountUnbuffered = 0;
        try (Reader r = new FileReader(file.toFile(), StandardCharsets.UTF_8)) {
            int c;
            while ((c = r.read()) != -1) {
                charCountUnbuffered++;
            }
        }
        long unbufferedMillis = (System.nanoTime() - startUnbuffered) / 1_000_000;

        // Buffered: FileReader wrapped in BufferedReader batches reads into a large internal buffer.
        long startBuffered = System.nanoTime();
        int charCountBuffered = 0;
        try (BufferedReader r = new BufferedReader(new FileReader(file.toFile(), StandardCharsets.UTF_8))) {
            int c;
            while ((c = r.read()) != -1) {
                charCountBuffered++;
            }
        }
        long bufferedMillis = (System.nanoTime() - startBuffered) / 1_000_000;

        System.out.println("Unbuffered read() char-by-char: " + unbufferedMillis + " ms (" + charCountUnbuffered + " chars)");
        System.out.println("Buffered   read() char-by-char: " + bufferedMillis + " ms (" + charCountBuffered + " chars)");
        System.out.printf("Speedup: %.1fx%n", unbufferedMillis / (double) Math.max(bufferedMillis, 1));
    }

    /** Writing with one charset and reading with another produces real, silent mojibake. */
    private static void demoCharsetMismatch(Path dir) throws IOException {
        String text = "café résumé naïve";
        Path file = dir.resolve("charset.txt");

        System.out.println("Demo B: charset mismatch (source string: \"" + text + "\")");

        // Write as UTF-8.
        try (OutputStreamWriter w = new OutputStreamWriter(Files.newOutputStream(file), StandardCharsets.UTF_8)) {
            w.write(text);
        }
        long utf8Bytes = Files.size(file);

        String readBackUtf8;
        try (InputStreamReader r = new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8)) {
            readBackUtf8 = readAll(r);
        }

        String readBackWrongCharset;
        try (InputStreamReader r = new InputStreamReader(Files.newInputStream(file), StandardCharsets.ISO_8859_1)) {
            readBackWrongCharset = readAll(r);
        }

        System.out.println("Written as UTF-8, size on disk: " + utf8Bytes + " bytes (source string is " + text.length() + " chars)");
        System.out.println("Read back with the SAME charset (UTF-8):     \"" + readBackUtf8 + "\" -- correct, round-trips exactly");
        System.out.println("Read back with the WRONG charset (ISO-8859-1): \"" + readBackWrongCharset + "\" -- silent corruption, no exception thrown");
        System.out.println("Matches original? UTF-8 read: " + text.equals(readBackUtf8) + " | ISO-8859-1 read: " + text.equals(readBackWrongCharset));
    }

    private static String readAll(Reader r) throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = r.read()) != -1) {
            sb.append((char) c);
        }
        return sb.toString();
    }

    /** try-with-resources: a close() failure after the body already threw is recorded as a suppressed exception, not lost. */
    private static void demoSuppressedExceptionOnClose() {
        System.out.println("Demo C: suppressed exceptions in try-with-resources");
        try {
            useResourceThatFailsOnClose();
        } catch (Exception e) {
            System.out.println("Caught primary exception: " + e.getMessage());
            for (Throwable suppressed : e.getSuppressed()) {
                System.out.println("  Suppressed (from close()): " + suppressed.getMessage());
            }
        }
    }

    private static void useResourceThatFailsOnClose() throws Exception {
        try (FailingCloseable resource = new FailingCloseable()) {
            throw new IllegalStateException("body failed before close() ever ran");
        }
    }

    private static final class FailingCloseable implements AutoCloseable {
        @Override
        public void close() throws Exception {
            throw new IOException("close() also failed");
        }
    }

    /** NIO.2: Path/Files as the modern, higher-level replacement for File-based I/O. */
    private static void demoNio2Basics(Path dir) throws IOException {
        System.out.println("Demo D: NIO.2 (java.nio.file) basics");
        Path file = dir.resolve("nio2.txt");
        List<String> lines = List.of("first line", "second line", "third line");

        Files.write(file, lines, StandardCharsets.UTF_8);
        List<String> readBack = Files.readAllLines(file, StandardCharsets.UTF_8);

        System.out.println("Files.write() wrote " + lines.size() + " lines; Files.exists(): " + Files.exists(file));
        System.out.println("Files.readAllLines() returned: " + readBack);
        System.out.println("Files.size(): " + Files.size(file) + " bytes");
        System.out.println("Matches what was written? " + lines.equals(readBack));
    }
}
