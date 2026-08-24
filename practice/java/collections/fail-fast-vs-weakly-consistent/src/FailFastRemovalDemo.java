import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * Real, executed proof that ArrayList's fail-fast behavior is "best effort,"
 * NOT a guarantee -- the JLS/Javadoc's own wording. It throws
 * ConcurrentModificationException for most structural modifications during
 * iteration, but a well-known real quirk (removing the SECOND-TO-LAST
 * element) slips through without any exception at all, because of exactly
 * how Itr.hasNext()/next() check the cursor against size and modCount.
 */
public class FailFastRemovalDemo {

    public static void main(String[] args) {
        caseNormalRemovalThrowsCme();
        caseSecondToLastRemovalDoesNotThrow();
        caseCorrectFixViaIteratorRemove();
    }

    static void caseNormalRemovalThrowsCme() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        System.out.println("== Case A: list.remove() on a NON-second-to-last element during for-each ==");
        try {
            for (Integer i : list) {
                if (i == 2) {
                    list.remove(i); // structural modification mid-iteration
                }
            }
            System.out.println("No exception thrown (did NOT happen)");
        } catch (ConcurrentModificationException e) {
            System.out.println("Real ConcurrentModificationException thrown, as expected: " + e);
        }
    }

    static void caseSecondToLastRemovalDoesNotThrow() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        System.out.println("\n== Case B: list.remove() on the SECOND-TO-LAST element (the classic quirk) ==");
        boolean threw = false;
        try {
            for (Integer i : list) {
                if (i == 4) { // second-to-last of [1,2,3,4,5]
                    list.remove(i);
                }
            }
        } catch (ConcurrentModificationException e) {
            threw = true;
        }
        System.out.println("Exception thrown: " + threw + " -- fail-fast is best-effort, NOT a guarantee."
                + " Result list (silently corrupted, one element short of what a caller might expect an error for): " + list);
    }

    static void caseCorrectFixViaIteratorRemove() {
        List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
        System.out.println("\n== Case C: the correct fix -- Iterator.remove() itself ==");
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            int value = it.next();
            if (value == 2 || value == 4) {
                it.remove(); // updates expectedModCount to match -- no CME
            }
        }
        System.out.println("No exception; correctly removed 2 and 4: " + list);
    }
}
