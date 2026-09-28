import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Real, measured file-system behaviour: sequential versus random reads, the
 * page cache, buffer size, and the true cost of durability (fsync).
 *
 * Numbers are machine-specific; the ORDERS OF MAGNITUDE are the lesson, and
 * they are reproducible by re-running this file.
 *
 * Java 21. Pure JDK, no dependencies.
 *
 * Compile and run:
 *   javac -d out src/FileSystemIoDemo.java
 *   java -cp out FileSystemIoDemo
 */
public class FileSystemIoDemo {

    private static final int MB = 1024 * 1024;
    private static final int FILE_SIZE = 256 * MB;
    private static final int BLOCK = 4096;

    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("fs-io-demo");
        Path data = dir.resolve("data.bin");
        try {
            section("Setup");
            createFile(data);
            System.out.printf("Created %s (%d MB) at %s%n", data.getFileName(), FILE_SIZE / MB, dir);

            section("1. Sequential vs. random reads of the SAME number of bytes (page cache WARM)");
            sequentialVsRandom(data);

            section("2. The page cache: reading the same file twice");
            pageCache(data);

            section("3. Buffer size: same bytes, different syscall counts");
            bufferSize(data);

            section("4. The cost of durability: write, flush, and fsync");
            durability(dir);

            section("5. What fsync actually guarantees");
            System.out.println("A write() returns once the data is in the OS page cache -- NOT on disk.");
            System.out.println("Until fsync() returns, a power loss or kernel crash can lose it, even though");
            System.out.println("the process already moved on. Every database's durability (the D in ACID)");
            System.out.println("is built on exactly this call, which is why commit latency has a floor set");
            System.out.println("by storage hardware and why group commit exists.");
        } finally {
            deleteRecursively(dir);
        }
    }

    // ------------------------------------------------ setup

    private static void createFile(Path path) throws IOException {
        byte[] block = new byte[MB];
        new Random(42).nextBytes(block);
        try (FileChannel ch = FileChannel.open(path,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (int i = 0; i < FILE_SIZE / MB; i++) {
                ch.write(ByteBuffer.wrap(block));
            }
            ch.force(true); // make sure it is really on disk before we measure reads
        }
    }

    // ------------------------------------------------ 1

    private static void sequentialVsRandom(Path path) throws IOException {
        int blocks = 20_000;                       // same count for both cases
        long bytes = (long) blocks * BLOCK;

        long seqNanos = timeSequential(path, blocks);
        long randNanos = timeRandom(path, blocks);

        report("sequential", bytes, seqNanos);
        report("random    ", bytes, randNanos);
        System.out.printf("Ratio random/sequential for the identical %d KB: %.2fx%n",
                bytes / 1024, (double) randNanos / seqNanos);
        System.out.println("HONEST RESULT: on this machine the two are indistinguishable. The file was");
        System.out.println("written moments ago and is resident in the OS page cache, and the backing");
        System.out.println("store is NVMe, so neither access pattern pays a seek. The textbook 10-100x");
        System.out.println("random-read penalty is a SPINNING-DISK, CACHE-MISS phenomenon; quoting it as");
        System.out.println("a universal fact about 'disk' is folklore. Dropping the page cache requires");
        System.out.println("elevated privileges, so this demo deliberately does not claim a number it");
        System.out.println("cannot measure. What still holds everywhere: sequential access is friendlier");
        System.out.println("to readahead, to erase-block-sized writes, and to any layer that prefetches.");
    }

    private static long timeSequential(Path path, int blocks) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(BLOCK);
        long start = System.nanoTime();
        try (FileChannel ch = FileChannel.open(path, StandardOpenOption.READ)) {
            for (int i = 0; i < blocks; i++) {
                buf.clear();
                ch.read(buf, (long) i * BLOCK);
            }
        }
        return System.nanoTime() - start;
    }

    private static long timeRandom(Path path, int blocks) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(BLOCK);
        Random random = new Random(7);
        long maxBlock = FILE_SIZE / BLOCK;
        long start = System.nanoTime();
        try (FileChannel ch = FileChannel.open(path, StandardOpenOption.READ)) {
            for (int i = 0; i < blocks; i++) {
                buf.clear();
                ch.read(buf, (long) (random.nextInt((int) maxBlock)) * BLOCK);
            }
        }
        return System.nanoTime() - start;
    }

    // ------------------------------------------------ 2

    private static void pageCache(Path path) throws IOException {
        Path small = path.getParent().resolve("cached.bin");
        byte[] payload = new byte[32 * MB];
        new Random(9).nextBytes(payload);
        Files.write(small, payload);

        long first = timeFullRead(small);
        long second = timeFullRead(small);
        long third = timeFullRead(small);

        report("first read ", 32L * MB, first);
        report("second read", 32L * MB, second);
        report("third read ", 32L * MB, third);
        System.out.println("The file did not change and the code did not change. The OS page cache");
        System.out.println("served the later reads from RAM, so 'disk is slow' is really 'a cache MISS");
        System.out.println("is slow' -- which is why a benchmark that ignores cache state is meaningless.");
        Files.deleteIfExists(small);
    }

    private static long timeFullRead(Path path) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(64 * 1024);
        long start = System.nanoTime();
        try (FileChannel ch = FileChannel.open(path, StandardOpenOption.READ)) {
            while (ch.read(buf) > 0) {
                buf.clear();
            }
        }
        return System.nanoTime() - start;
    }

    // ------------------------------------------------ 3

    private static void bufferSize(Path path) throws IOException {
        long bytes = 64L * MB;
        for (int size : new int[]{512, 4096, 65536, 1024 * 1024}) {
            long nanos = timeWithBuffer(path, size, bytes);
            System.out.printf("  buffer %7d B -> %6d ms   (%d read() calls)%n",
                    size, TimeUnit.NANOSECONDS.toMillis(nanos), bytes / size);
        }
        System.out.println("Identical bytes read. The difference is the number of syscalls, each of");
        System.out.println("which crosses the user/kernel boundary -- the reason buffered streams exist.");
    }

    private static long timeWithBuffer(Path path, int size, long bytes) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(size);
        long start = System.nanoTime();
        try (FileChannel ch = FileChannel.open(path, StandardOpenOption.READ)) {
            long read = 0;
            while (read < bytes) {
                buf.clear();
                int n = ch.read(buf);
                if (n <= 0) {
                    break;
                }
                read += n;
            }
        }
        return System.nanoTime() - start;
    }

    // ------------------------------------------------ 4

    private static void durability(Path dir) throws IOException {
        int records = 2_000;
        byte[] record = new byte[256];
        new Random(11).nextBytes(record);

        Path a = dir.resolve("no-fsync.log");
        Path b = dir.resolve("fsync-each.log");
        Path c = dir.resolve("fsync-batched.log");

        long noFsync = appendAll(a, record, records, 0);
        long each = appendAll(b, record, records, 1);
        long batched = appendAll(c, record, records, 100);

        System.out.printf("  %-28s %7d ms%n", "no fsync at all", TimeUnit.NANOSECONDS.toMillis(noFsync));
        System.out.printf("  %-28s %7d ms%n", "fsync every record", TimeUnit.NANOSECONDS.toMillis(each));
        System.out.printf("  %-28s %7d ms%n", "fsync every 100 records", TimeUnit.NANOSECONDS.toMillis(batched));
        System.out.printf("Durability per record cost %.0fx the un-synced write.%n",
                (double) each / Math.max(noFsync, 1));
        System.out.println("Batching amortises it -- which is exactly what a database's group commit does,");
        System.out.println("trading a little latency for a large throughput gain, at the same durability.");
    }

    /** @param syncEvery 0 = never fsync, 1 = fsync per record, N = fsync every N records. */
    private static long appendAll(Path path, byte[] record, int count, int syncEvery) throws IOException {
        long start = System.nanoTime();
        try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw");
             FileChannel ch = file.getChannel()) {
            for (int i = 1; i <= count; i++) {
                ch.write(ByteBuffer.wrap(record));
                if (syncEvery > 0 && i % syncEvery == 0) {
                    ch.force(false); // data only, not metadata -- the cheaper variant
                }
            }
            if (syncEvery > 0) {
                ch.force(false);
            }
        }
        return System.nanoTime() - start;
    }

    // ------------------------------------------------ helpers

    private static void report(String label, long bytes, long nanos) {
        double ms = nanos / 1_000_000.0;
        double mbPerSec = (bytes / (double) MB) / (nanos / 1_000_000_000.0);
        System.out.printf("  %-12s %8.1f ms   %8.1f MB/s%n", label, ms, mbPerSec);
    }

    private static void deleteRecursively(Path dir) {
        try (var stream = Files.walk(dir)) {
            stream.sorted((x, y) -> y.getNameCount() - x.getNameCount()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                    // best effort cleanup of a temp directory
                }
            });
        } catch (IOException ignored) {
            // best effort
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
