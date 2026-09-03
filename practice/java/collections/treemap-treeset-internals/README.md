# TreeMap/TreeSet internals (T-203) — runnable verification

Real, executed Java backing [`syllabus/02-java/collections/treemap-treeset-and-navigable-hierarchy.md`](../../../../syllabus/02-java/collections/treemap-treeset-and-navigable-hierarchy.md) (T-203). Also closes two real, tracked Phase 1 audit defects: the inverted `Set` hierarchy diagram, and `NavigableSet` miscategorized as a peer implementation instead of the interface `TreeSet` actually implements.

## Setup and run

```bash
cd practice/java/collections/treemap-treeset-internals
mkdir -p out
javac -d out src/*.java
java --add-opens java.base/java.util=ALL-UNNAMED -cp out RedBlackHeightDemo
java -cp out NavigableSetDemo
```

`RedBlackHeightDemo` needs `--add-opens` — it reflects into `TreeMap`'s private `root` field and `TreeMap.Entry`'s private `left`/`right` fields to measure the real tree height. `NavigableSetDemo` needs no special flags — every method it calls is public API.

## Real observed output (last run)

### `RedBlackHeightDemo` — real, reflective height measurement, ascending (adversarial) insertion

```
n	TreeMap height (real, reflective)	Naive BST height (real)	2*log2(n+1)
10	5	10	6.9
100	11	100	13.3
1000	17	1000	19.9
10000	24	10000	26.6
100000	31	100000	33.2
```

At n=100,000, `TreeMap`'s real height is 31 — a naive, unbalanced BST inserting the identical ascending sequence has a real height of exactly 100,000, since it degenerates into a straight linked list. `TreeMap`'s real height never once exceeded the Red-Black tree's theoretical worst-case bound (2·log₂(n+1)) at any measured size.

### `NavigableSetDemo` — real interface-hierarchy proof, real navigation output

```
NavigableSet<Integer> set = new TreeSet<>(...) -- compiles: [10, 20, 30, 40, 50]

== Real NavigableSet methods, real output ==
floor(25)    = 20   (greatest element <= 25)
ceiling(25)  = 30   (smallest element >= 25)
lower(30)    = 20   (greatest element < 30, strictly)
higher(30)   = 40   (smallest element > 30, strictly)
first()      = 10
last()       = 50
descendingSet() = [50, 40, 30, 20, 10]
subSet(20, true, 40, true) = [20, 30, 40]
pollFirst()  = 10   set is now: [20, 30, 40, 50]

== The real interface chain (java.lang.Class.getInterfaces / getSuperclass) ==
TreeSet implements/extends:
  extends AbstractSet
  implements NavigableSet
  implements Cloneable
  implements Serializable

TreeMap implements/extends:
  extends AbstractMap
  implements NavigableMap
  implements Cloneable
  implements Serializable

== NavigableMap: the same pattern for Map ==
floorEntry(25)   = 10=ten
ceilingEntry(25) = 30=thirty
firstKey()       = 10
descendingMap()  = {50=fifty, 30=thirty, 10=ten}
```

`TreeSet.class.getInterfaces()` returning `[NavigableSet, Cloneable, Serializable]` is the real, direct, reflective proof: `NavigableSet` is the interface `TreeSet` implements, never a sibling concrete class.
