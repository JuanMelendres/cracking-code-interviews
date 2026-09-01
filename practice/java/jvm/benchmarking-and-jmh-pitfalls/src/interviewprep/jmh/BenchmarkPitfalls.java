package interviewprep.jmh;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

/**
 * Real JMH benchmarks demonstrating the two most common ways a naive
 * microbenchmark reports a fictitious number: dead-code elimination and
 * constant folding. Each pitfall has a "broken" method (the naive version)
 * and a "fixed" method doing the identical real work correctly measured.
 *
 * <p>Deliberately mirrors the shape of the JMH project's own official
 * samples (JMHSample_08_DeadCode, JMHSample_10_ConstantFold) rather than an
 * invented loop, because {@code Math.log} is a well-documented HotSpot
 * intrinsic whose folding/elimination behavior under these exact conditions
 * is established and reliably reproducible -- not something this pack is
 * guessing at.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class BenchmarkPitfalls {

    /** A real @State field: read at runtime, not knowable to javac at compile time. */
    double x = Math.PI;

    /**
     * A compile-time constant per JLS 15.29: a {@code static final double}
     * with a constant initializer. javac inlines its value as a literal at
     * every use site -- this field's *name* survives to bytecode, but every
     * read of it is compiled as if the literal 3.141592653589793 had been
     * written directly.
     */
    static final double CONSTANT_X = Math.PI;

    // ---- Pitfall 1: dead-code elimination ----

    @Benchmark
    public void broken_deadCodeEliminated() {
        // BUG: the result is computed and then discarded. Nothing in this
        // method has an observable side effect, so the JIT is free to prove
        // the entire computation is dead code and eliminate it -- this
        // "benchmark" ends up measuring an empty method body.
        Math.log(x);
    }

    @Benchmark
    public double fixed_returnResult() {
        // FIX: returning the result hands it to JMH, which consumes it
        // (internally, via the same mechanism a manual Blackhole.consume
        // call provides) -- the computation now has an observable
        // consequence and cannot be eliminated.
        return Math.log(x);
    }

    // ---- Pitfall 2: constant folding ----

    @Benchmark
    public double baseline_realComputation() {
        return Math.log(x);
    }

    @Benchmark
    public double broken_constantFolded() {
        // BUG: CONSTANT_X is a compile-time constant, so every call site
        // reads the identical literal value on every single invocation.
        // The JIT can prove this call is pure and its input never varies,
        // and hoists/caches the result -- this "benchmark" measures a
        // precomputed constant, not a real Math.log call.
        return Math.log(CONSTANT_X);
    }
}
