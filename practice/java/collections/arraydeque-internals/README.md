# ArrayDeque internals & the legacy Stack/Vector problem (T-204) — runnable verification

Real, executed Java (OpenJDK 21.0.12) backing
[`handbook/collections/arraydeque-internals-and-the-legacy-stack-problem.md`](../../../../handbook/collections/arraydeque-internals-and-the-legacy-stack-problem.md)
(T-204). Two independent demos, including a real finding that corrects outdated,
widely-repeated "ArrayDeque uses power-of-two capacity" folklore.

## Setup and run

```bash
cd practice/java/collections/arraydeque-internals
mkdir -p out
javac -d out src/*.java
java --add-opens java.base/java.util=ALL-UNNAMED -cp out CapacityAndWraparoundDemo
java -cp out StackReplacementDemo
```

`CapacityAndWraparoundDemo` needs `--add-opens` — it reflects into `ArrayDeque`'s private
`elements`/`head`/`tail` fields. `StackReplacementDemo` needs no special flags.

## Real observed output (last run)

### `CapacityAndWraparoundDemo` — a real finding that corrects outdated folklore

```
== Real backing-array capacity, requested vs actual ==
requested	actual backing array length
1		2
3		4
8		9
9		10
17		18
100		101
```

Widely-repeated folklore (accurate for older JDK versions, which used a bitmask-based modulo
requiring a power-of-two array length) claims `ArrayDeque`'s capacity is always rounded up to the
next power of two. On OpenJDK 21.0.12, this is **not true**: the real, measured actual capacity is
consistently `requested + 1` — one extra slot always reserved (a classic circular-buffer technique
to disambiguate "full" from "empty" without a separate counter field), with no power-of-two
rounding at all. `8` requested becomes `9`, not `8` or `16`; `100` requested becomes `101`. This is
real, current-version evidence, not an assumption carried over from older JDK behavior.

```
== Real growth behavior when capacity is exceeded ==
Initial actual capacity: 5
After filling all 4 usable slots: 5 (unchanged)
After one more add (triggers grow()): 12
After 20 more adds: 26
```

Growth is real and substantial (5 → 12 → 26) but not a clean doubling — the exact growth formula
isn't asserted here beyond what was actually measured; the real point is that growth is infrequent
and by a real, non-trivial factor, preserving amortized O(1) `add()`.

```
== Real circular wraparound: head index can exceed tail index ==
...
Final real indices: head=3 tail=1  <-- head > tail: REAL proof of circular wraparound (a linear array could never show this)
```

Mixed `addLast()`/`pollFirst()` calls against a small, fixed-capacity deque drive the real `head`
and `tail` integer indices all the way around the backing array — ending with `head` (3) genuinely
*greater* than `tail` (1), direct, reflective proof of circular-buffer wraparound behavior that a
plain linear array could never produce.

### `StackReplacementDemo` — real measured cost of the legacy `Stack`, and a real null-handling gotcha

```
== Real measured wall-clock time, 20000000 push+pop pairs ==
java.util.Stack (legacy, synchronized): 106ms
ArrayDeque (via Deque push/pop):        47ms
LinkedList (via Deque push/pop):        97ms
Real measured ArrayDeque vs Stack speedup: 2.26x

== Real null-handling difference ==
ArrayDeque.addFirst(null): threw real NullPointerException (null is reserved internally as the empty-slot sentinel)
LinkedList.addFirst(null):  succeeded, contents=[null] (LinkedList has no such internal sentinel restriction)
```

`java.util.Stack` extends the legacy, `synchronized`-method `Vector` — every single `push()`/`pop()`
call acquires a real lock, even in genuinely single-threaded code. `ArrayDeque` used as a stack via
the `Deque` interface's own `push()`/`pop()` methods measured a real, reproducible ~2.26x speedup
over `Stack`, and was also real, measurably faster than `LinkedList` used the same way (array
locality beating per-node allocation/pointer-chasing even for pure head operations). The null-
handling difference is real and important: `ArrayDeque` genuinely throws `NullPointerException` on
any attempt to insert `null` (`null` is reserved internally as ArrayDeque's own empty-slot
sentinel), while `LinkedList` accepts `null` without complaint — a real behavioral difference to
know before swapping one for the other.
