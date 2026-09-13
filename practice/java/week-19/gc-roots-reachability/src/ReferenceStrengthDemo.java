import java.lang.ref.*;

// Real demo of the reference-strength hierarchy and reachability: an object
// with no strong references left is a GC candidate; WeakReference is
// cleared as soon as the collector runs; PhantomReference never returns the
// referent at all and exists purely for post-collection cleanup
// notification via a ReferenceQueue -- the mechanism behind DirectByteBuffer
// cleaners and java.lang.ref.Cleaner.
public class ReferenceStrengthDemo {

    static class Payload {
        final int id;
        Payload(int id) { this.id = id; }
        @Override public String toString() { return "Payload#" + id; }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Strong reference: survives GC ===");
        Payload strong = new Payload(1);
        System.gc();
        Thread.sleep(100);
        System.out.println("strong reference still points to: " + strong);

        System.out.println();
        System.out.println("=== Weak reference: cleared once no strong reference remains ===");
        Payload target = new Payload(2);
        WeakReference<Payload> weakRef = new WeakReference<>(target);
        System.out.println("before nulling strong ref, weakRef.get() = " + weakRef.get());
        target = null; // remove the only strong reference -- object is now unreachable
        System.gc();
        Thread.sleep(100);
        System.out.println("after System.gc(), weakRef.get() = " + weakRef.get());

        System.out.println();
        System.out.println("=== Soft reference: JVM prefers to keep it under normal memory pressure ===");
        Payload softTarget = new Payload(3);
        SoftReference<Payload> softRef = new SoftReference<>(softTarget);
        softTarget = null;
        System.gc();
        Thread.sleep(100);
        System.out.println("after System.gc() with no real memory pressure, softRef.get() = " + softRef.get()
                + "  (soft refs are only guaranteed cleared before an OutOfMemoryError, not on a routine GC)");

        System.out.println();
        System.out.println("=== Phantom reference: get() ALWAYS returns null; enqueued only after collection ===");
        ReferenceQueue<Payload> queue = new ReferenceQueue<>();
        Payload phantomTarget = new Payload(4);
        PhantomReference<Payload> phantomRef = new PhantomReference<>(phantomTarget, queue);
        System.out.println("phantomRef.get() = " + phantomRef.get() + "  (always null, by design -- phantom refs cannot resurrect the referent)");
        phantomTarget = null;
        System.gc();
        Reference<? extends Payload> enqueued = queue.remove(2000);
        System.out.println("queue.remove() returned: " + (enqueued == phantomRef ? "the phantom reference itself, now enqueued" : "null / timeout"));
    }
}
