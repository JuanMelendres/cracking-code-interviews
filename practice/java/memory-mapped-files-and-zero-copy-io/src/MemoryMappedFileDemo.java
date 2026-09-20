import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/** Real file I/O, two ways, against the identical real file on disk:
 * ordinary {@link FileChannel} reads (each a real syscall crossing into
 * kernel space) versus a real {@link MappedByteBuffer} (the file's pages
 * mapped directly into this process's virtual address space -- reads
 * become ordinary memory accesses, with the OS page cache doing the
 * actual disk I/O transparently, on demand, per page). Two access
 * patterns measured: sequential (a modest difference) and random (where
 * mmap's real advantage shows up, per-access syscall overhead removed
 * entirely). */
public class MemoryMappedFileDemo {

    static final long FILE_SIZE = 512L * 1024 * 1024; // 512MB
    static final int RANDOM_ACCESSES = 2_000_000;

    public static void main(String[] args) throws Exception {
        Path file = Files.createTempFile("mmap-demo-", ".bin");
        file.toFile().deleteOnExit();
        System.out.println("Real temp file: " + file + " (" + FILE_SIZE + " bytes)");
        generateFile(file);

        System.out.println("\n=== Sequential full-file checksum, " + FILE_SIZE + " bytes ===");
        long[] seqResult = new long[2];
        long seqChannelMs = sequentialChannelChecksum(file, seqResult, 0);
        long seqMmapMs = sequentialMmapChecksum(file, seqResult, 1);
        System.out.println("FileChannel bulk reads:  " + seqChannelMs + "ms");
        System.out.println("MappedByteBuffer scan:   " + seqMmapMs + "ms");
        System.out.println("Checksums match: " + (seqResult[0] == seqResult[1]));

        System.out.println("\n=== Random access, " + RANDOM_ACCESSES + " reads of 8 bytes each ===");
        long[] offsets = randomOffsets(RANDOM_ACCESSES);
        long[] randResult = new long[2];
        long randChannelMs = randomChannelReads(file, offsets, randResult, 0);
        long randMmapMs = randomMmapReads(file, offsets, randResult, 1);
        System.out.println("FileChannel positional reads (1 syscall each): " + randChannelMs + "ms");
        System.out.println("MappedByteBuffer random access (0 syscalls):   " + randMmapMs + "ms");
        System.out.println("Sums match: " + (randResult[0] == randResult[1]));
        System.out.printf("Random-access speedup: %.2fx%n", (double) randChannelMs / randMmapMs);

        Files.deleteIfExists(file);
    }

    static void generateFile(Path file) throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "rw")) {
            raf.setLength(FILE_SIZE);
            FileChannel channel = raf.getChannel();
            ByteBuffer chunk = ByteBuffer.allocate(1024 * 1024);
            long position = 0;
            while (position < FILE_SIZE) {
                chunk.clear();
                while (chunk.hasRemaining()) {
                    chunk.put((byte) (position % 256)); // a real, varying byte pattern
                    position++;
                }
                chunk.flip();
                channel.write(chunk);
            }
        }
    }

    static long sequentialChannelChecksum(Path file, long[] result, int slot) throws Exception {
        long start = System.nanoTime();
        long checksum = 0;
        try (FileChannel channel = FileChannel.open(file)) {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            while (channel.read(buffer) != -1) {
                buffer.flip();
                while (buffer.hasRemaining()) checksum += buffer.get();
                buffer.clear();
            }
        }
        result[slot] = checksum;
        return (System.nanoTime() - start) / 1_000_000;
    }

    static long sequentialMmapChecksum(Path file, long[] result, int slot) throws Exception {
        long start = System.nanoTime();
        long checksum = 0;
        try (FileChannel channel = FileChannel.open(file)) {
            MappedByteBuffer mapped = channel.map(FileChannel.MapMode.READ_ONLY, 0, FILE_SIZE);
            for (long i = 0; i < FILE_SIZE; i++) checksum += mapped.get((int) i);
        }
        result[slot] = checksum;
        return (System.nanoTime() - start) / 1_000_000;
    }

    static long[] randomOffsets(int count) {
        Random random = new Random(42);
        long[] offsets = new long[count];
        for (int i = 0; i < count; i++) {
            offsets[i] = (long) (random.nextDouble() * (FILE_SIZE - 8));
        }
        return offsets;
    }

    static long randomChannelReads(Path file, long[] offsets, long[] result, int slot) throws Exception {
        long start = System.nanoTime();
        long sum = 0;
        try (FileChannel channel = FileChannel.open(file)) {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            for (long offset : offsets) {
                buffer.clear();
                channel.read(buffer, offset); // one real positional-read syscall per access
                buffer.flip();
                sum += buffer.getLong();
            }
        }
        result[slot] = sum;
        return (System.nanoTime() - start) / 1_000_000;
    }

    static long randomMmapReads(Path file, long[] offsets, long[] result, int slot) throws Exception {
        long start = System.nanoTime();
        long sum = 0;
        try (FileChannel channel = FileChannel.open(file)) {
            MappedByteBuffer mapped = channel.map(FileChannel.MapMode.READ_ONLY, 0, FILE_SIZE);
            for (long offset : offsets) {
                sum += mapped.getLong((int) offset); // an ordinary memory read -- no syscall
            }
        }
        result[slot] = sum;
        return (System.nanoTime() - start) / 1_000_000;
    }
}
