import java.util.ArrayList;
import java.util.List;

public class CloneDemo {

    // Does NOT implement Cloneable -- calling super.clone() must throw CloneNotSupportedException.
    static class NotCloneable {
        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    // Implements Cloneable but relies on the default Object.clone() -- a SHALLOW copy:
    // primitive/String fields are copied by value, but the List reference is copied
    // as-is, so the original and the clone end up sharing the SAME list instance.
    static class ShallowTeam implements Cloneable {
        String name;
        List<String> members;

        ShallowTeam(String name, List<String> members) {
            this.name = name;
            this.members = members;
        }

        @Override
        public ShallowTeam clone() {
            try {
                return (ShallowTeam) super.clone(); // field-by-field copy, NOT recursive
            } catch (CloneNotSupportedException e) {
                throw new AssertionError("Cloneable is implemented, this can't happen", e);
            }
        }
    }

    // Overrides clone() to ALSO clone the mutable field -- a real deep copy for this
    // one level of nesting (members is no longer shared between original and clone).
    static class DeepTeam implements Cloneable {
        String name;
        List<String> members;

        DeepTeam(String name, List<String> members) {
            this.name = name;
            this.members = members;
        }

        @Override
        public DeepTeam clone() {
            try {
                DeepTeam copy = (DeepTeam) super.clone();
                copy.members = new ArrayList<>(this.members); // deep-copy the mutable field
                return copy;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError("Cloneable is implemented, this can't happen", e);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("== A class NOT implementing Cloneable: super.clone() throws ==");
        try {
            new NotCloneable().clone();
            System.out.println("no exception (unexpected)");
        } catch (CloneNotSupportedException e) {
            System.out.println("clone() threw CloneNotSupportedException  (Cloneable was never implemented)");
        }

        System.out.println();
        System.out.println("== Object.clone()'s default behavior is a SHALLOW copy ==");
        ShallowTeam originalShallow = new ShallowTeam("Backend", new ArrayList<>(List.of("alice", "bob")));
        ShallowTeam clonedShallow = originalShallow.clone();
        System.out.println("original.members before mutating the CLONE: " + originalShallow.members);
        clonedShallow.members.add("mallory"); // mutate the CLONE's list
        System.out.println("original.members AFTER clone.members.add(\"mallory\"): " + originalShallow.members
                + "  (changed! shallow clone copied the LIST REFERENCE, not the list itself)");
        System.out.println("originalShallow.members == clonedShallow.members: "
                + (originalShallow.members == clonedShallow.members) + "  (same object, confirmed by reference equality)");

        System.out.println();
        System.out.println("== Overriding clone() to deep-copy the mutable field fixes it ==");
        DeepTeam originalDeep = new DeepTeam("Backend", new ArrayList<>(List.of("alice", "bob")));
        DeepTeam clonedDeep = originalDeep.clone();
        System.out.println("original.members before mutating the CLONE: " + originalDeep.members);
        clonedDeep.members.add("mallory");
        System.out.println("original.members AFTER clone.members.add(\"mallory\"): " + originalDeep.members
                + "  (unchanged -- clone() made an independent copy of the list)");
        System.out.println("originalDeep.members == clonedDeep.members: "
                + (originalDeep.members == clonedDeep.members) + "  (different objects, confirmed by reference equality)");
    }
}
