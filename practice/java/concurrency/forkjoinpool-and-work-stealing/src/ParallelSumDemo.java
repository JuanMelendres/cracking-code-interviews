import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Real, measured fork/join speedup for a genuinely CPU-bound per-element
 * computation (not plain addition, which is memory-bandwidth-bound and
 * shows little real parallel speedup). Correctness is verified against a
 * real sequential baseline before any timing claim is made.
 */
public class ParallelSumDemo {

    static final int SIZE = 20_000_000;
    static final int SEQUENTIAL_THRESHOLD = 100_000;

    public static void main(String[] args) {
        double[] data = new double[SIZE];
        for (int i = 0; i < SIZE; i++) data[i] = i + 1.0;

        long seqStart = System.currentTimeMillis();
        double sequentialResult = sequentialSum(data, 0, data.length);
        long seqElapsed = System.currentTimeMillis() - seqStart;

        ForkJoinPool pool = new ForkJoinPool(); // default parallelism = availableProcessors()
        long parStart = System.currentTimeMillis();
        double parallelResult = pool.invoke(new SumTask(data, 0, data.length));
        long parElapsed = System.currentTimeMillis() - parStart;

        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors()
                + ", pool parallelism: " + pool.getParallelism());
        System.out.println("Sequential result: " + sequentialResult + " (" + seqElapsed + "ms)");
        System.out.println("Parallel result:   " + parallelResult + " (" + parElapsed + "ms)");
        System.out.println("Results match (within floating-point tolerance): "
                + (Math.abs(sequentialResult - parallelResult) < 1.0));
        System.out.println("Real measured speedup: " + String.format("%.2fx", (double) seqElapsed / parElapsed));

        pool.shutdown();
    }

    // Deliberately CPU-bound per element: sqrt + trig, not plain addition.
    static double expensiveOp(double x) {
        return Math.sqrt(x) * Math.sin(x) * Math.cos(x);
    }

    static double sequentialSum(double[] data, int start, int end) {
        double sum = 0;
        for (int i = start; i < end; i++) sum += expensiveOp(data[i]);
        return sum;
    }

    static class SumTask extends RecursiveTask<Double> {
        final double[] data;
        final int start, end;

        SumTask(double[] data, int start, int end) {
            this.data = data;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Double compute() {
            int length = end - start;
            if (length <= SEQUENTIAL_THRESHOLD) {
                return sequentialSum(data, start, end);
            }
            int mid = start + length / 2;
            SumTask left = new SumTask(data, start, mid);
            SumTask right = new SumTask(data, mid, end);
            left.fork();               // asynchronously forks the left half
            double rightResult = right.compute(); // computes the right half on THIS thread
            double leftResult = left.join();       // waits for (or steals/helps with) the left half
            return leftResult + rightResult;
        }
    }
}
