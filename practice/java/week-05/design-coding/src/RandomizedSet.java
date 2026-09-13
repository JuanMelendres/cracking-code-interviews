import java.util.*;

// LC 380 -- Insert Delete GetRandom O(1).
// ArrayList holds the values (needed for O(1) random access by index);
// a HashMap tracks each value's current index in the list. Removal swaps
// the target with the LAST element before removing from the end, so no
// element ever has to shift -- that swap-with-last trick is the whole idea.
final class RandomizedSet {
    private final List<Integer> values = new ArrayList<>();
    private final Map<Integer, Integer> valueToIndex = new HashMap<>();
    private final Random random = new Random();

    boolean insert(int val) {
        if (valueToIndex.containsKey(val)) return false;
        valueToIndex.put(val, values.size());
        values.add(val);
        return true;
    }

    boolean remove(int val) {
        if (!valueToIndex.containsKey(val)) return false;
        int indexToRemove = valueToIndex.get(val);
        int lastIndex = values.size() - 1;
        int lastValue = values.get(lastIndex);

        values.set(indexToRemove, lastValue);
        valueToIndex.put(lastValue, indexToRemove);

        values.remove(lastIndex);
        valueToIndex.remove(val);
        return true;
    }

    int getRandom() {
        return values.get(random.nextInt(values.size()));
    }
}
