public class WarmupSpeedupDemo {
    static long compute(long x) {
        long r = x;
        for (int i = 0; i < 50; i++) r = (r * 31 + i) ^ (r >>> 3);
        return r;
    }

    public static void main(String[] args) {
        int batches = 60;
        int iterationsPerBatch = 50_000;
        long sink = 0;
        for (int b = 0; b < batches; b++) {
            long start = System.nanoTime();
            for (int i = 0; i < iterationsPerBatch; i++) sink += compute(i);
            long elapsedNs = System.nanoTime() - start;
            double nsPerOp = (double) elapsedNs / iterationsPerBatch;
            System.out.println("batch " + b + " ns/op=" + nsPerOp);
        }
        System.out.println("sink=" + sink);
    }
}
