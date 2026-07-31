import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MutableLeakDemo {

    // BROKEN: looks immutable (all fields final, no setters) but leaks
    // mutable internals through its constructor AND its getter.
    static final class LeakyEvent {
        private final Date when;
        private final List<String> attendees;

        LeakyEvent(Date when, List<String> attendees) {
            this.when = when;                 // no defensive copy on the way in
            this.attendees = attendees;        // no defensive copy on the way in
        }

        Date getWhen() { return when; }               // no defensive copy on the way out
        List<String> getAttendees() { return attendees; } // no defensive copy on the way out
    }

    // FIXED: defensive copy on construction AND on every getter that would
    // otherwise hand out a live reference to internal mutable state.
    static final class SafeEvent {
        private final Date when;
        private final List<String> attendees;

        SafeEvent(Date when, List<String> attendees) {
            this.when = new Date(when.getTime());
            this.attendees = new ArrayList<>(attendees);
        }

        Date getWhen() { return new Date(when.getTime()); }
        List<String> getAttendees() { return List.copyOf(attendees); }
    }

    public static void main(String[] args) {
        System.out.println("== Leak #1: caller mutates the Date AFTER construction ==");
        Date originalDate = new Date(1_700_000_000_000L);
        LeakyEvent leaky = new LeakyEvent(originalDate, new ArrayList<>(List.of("alice", "bob")));
        System.out.println("Event date right after construction: " + leaky.getWhen());
        originalDate.setTime(0L); // caller mutates the SAME Date object post-construction
        System.out.println("Event date after caller mutates the original Date object: " + leaky.getWhen()
                + "  (changed! the constructor kept a live reference, not a copy)");

        System.out.println();
        System.out.println("== Leak #2: caller mutates the List returned by the getter ==");
        LeakyEvent leaky2 = new LeakyEvent(new Date(), new ArrayList<>(List.of("carol")));
        System.out.println("attendees before external mutation: " + leaky2.getAttendees());
        leaky2.getAttendees().add("mallory"); // mutating what the getter returned
        System.out.println("attendees after calling getAttendees().add(\"mallory\") from OUTSIDE the class: "
                + leaky2.getAttendees() + "  (the object's own internal list was mutated by an outsider)");

        System.out.println();
        System.out.println("== The fixed, truly immutable version resists both leaks ==");
        Date safeDate = new Date(1_700_000_000_000L);
        SafeEvent safe = new SafeEvent(safeDate, new ArrayList<>(List.of("alice", "bob")));
        safeDate.setTime(0L);
        System.out.println("Event date after caller mutates the ORIGINAL Date passed to the constructor: "
                + safe.getWhen() + "  (unchanged -- the constructor copied it)");
        try {
            safe.getAttendees().add("mallory");
            System.out.println("no exception (unexpected)");
        } catch (UnsupportedOperationException e) {
            System.out.println("getAttendees().add(\"mallory\") threw UnsupportedOperationException"
                    + "  (List.copyOf() returns an immutable view -- mutation is rejected outright, not just copied)");
        }
    }
}
