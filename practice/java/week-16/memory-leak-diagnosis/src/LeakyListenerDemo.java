import java.util.*;
import java.util.concurrent.*;

/**
 * A real, classic listener-registration leak: short-lived "Session" objects
 * register themselves as listeners on a long-lived Subject and are never
 * unregistered when the session ends. The Subject's listener list grows
 * without bound even though every individual Session object is otherwise
 * garbage -- it is kept alive ONLY by the strong reference the Subject holds.
 *
 * Run with --fix to unregister listeners on session end (the correct version),
 * or with no argument to reproduce the leak.
 *
 * While running, in another terminal:
 *   jmap -histo:live <pid> | grep Session
 * at intervals, to watch Session instance counts either grow unbounded
 * (leaky) or stay flat (fixed).
 */
public class LeakyListenerDemo {

    interface Listener { void onEvent(String e); }

    static class Subject {
        // Real leak: no way to remove a listener, and callers never try.
        private final List<Listener> listeners = new CopyOnWriteArrayList<>();
        void register(Listener l) { listeners.add(l); }
        void unregister(Listener l) { listeners.remove(l); }
        void fire(String event) { for (Listener l : listeners) l.onEvent(event); }
        int listenerCount() { return listeners.size(); }
    }

    // A short-lived request/session object. In real systems this is exactly
    // the shape of a "leak by design accident" -- something per-request that
    // registers with something application-scoped.
    static class Session implements Listener {
        final long id;
        final byte[] payload = new byte[4096]; // give it real retained size
        Session(long id) { this.id = id; }
        @Override public void onEvent(String e) { /* no-op */ }
    }

    static final Subject APP_SCOPED_SUBJECT = new Subject();

    public static void main(String[] args) throws Exception {
        boolean fix = args.length > 0 && args[0].equals("--fix");
        long pid = ProcessHandle.current().pid();
        System.out.println("PID=" + pid + " mode=" + (fix ? "FIXED (unregisters on session end)" : "LEAKY (never unregisters)"));

        int totalSessions = 200_000;
        for (int i = 0; i < totalSessions; i++) {
            Session s = new Session(i);
            APP_SCOPED_SUBJECT.register(s);
            APP_SCOPED_SUBJECT.fire("session-" + i + "-started");
            // session "ends" here -- in a correct implementation this is where
            // unregister() must be called, or the Subject keeps it alive forever.
            if (fix) {
                APP_SCOPED_SUBJECT.unregister(s);
            }

            if (i % 20_000 == 0) {
                System.out.println("processed " + i + " sessions, current listener count = "
                        + APP_SCOPED_SUBJECT.listenerCount());
                Thread.sleep(50); // give an external jmap sample a window to fire
            }
        }
        System.out.println("done. FINAL listener count = " + APP_SCOPED_SUBJECT.listenerCount()
                + " (processed " + totalSessions + " sessions)");
        Thread.sleep(3000); // hold the process open for a final external sample / heap dump
    }
}
