public class DeoptDemo {
    interface Shape { double area(); }
    static class Circle implements Shape {
        double r; Circle(double r){this.r=r;}
        public double area(){ return Math.PI*r*r; }
    }
    static class Square implements Shape {
        double s; Square(double s){this.s=s;}
        public double area(){ return s*s; }
    }

    static double sumAreas(Shape[] shapes) {
        double total = 0;
        for (Shape s : shapes) total += s.area();
        return total;
    }

    public static void main(String[] args) {
        int n = 2_000_000;
        Shape[] monoBatch = new Shape[n];
        for (int i = 0; i < n; i++) monoBatch[i] = new Circle(i % 7 + 1);

        // Phase 1: warm up sumAreas() thoroughly with ONLY Circle -- JIT can
        // speculate a monomorphic call site and inline Circle.area() directly.
        long start = System.nanoTime();
        double t1 = sumAreas(monoBatch);
        long phase1Ns = System.nanoTime() - start;
        System.out.println("phase1 (mono, Circle only) total=" + t1 + " ns=" + phase1Ns);

        // Phase 2: same call site, same method, now with a MIX of Circle and
        // Square -- if the JIT had speculated monomorphic, this forces a
        // deoptimization back to an uncompiled/generic-dispatch version.
        Shape[] mixedBatch = new Shape[n];
        for (int i = 0; i < n; i++) mixedBatch[i] = (i % 2 == 0) ? new Circle(i % 7 + 1) : new Square(i % 5 + 1);
        start = System.nanoTime();
        double t2 = sumAreas(mixedBatch);
        long phase2Ns = System.nanoTime() - start;
        System.out.println("phase2 (mixed Circle+Square, right after mono warmup) total=" + t2 + " ns=" + phase2Ns);

        // Phase 3: run the SAME mixed workload again -- JIT should have
        // recompiled for megamorphic/bimorphic dispatch by now, so this
        // should be faster than phase 2's first exposure to the mix.
        start = System.nanoTime();
        double t3 = sumAreas(mixedBatch);
        long phase3Ns = System.nanoTime() - start;
        System.out.println("phase3 (mixed again, after recompilation) total=" + t3 + " ns=" + phase3Ns);
    }
}
