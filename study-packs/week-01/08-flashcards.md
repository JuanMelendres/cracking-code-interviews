# Flashcards — Week 1

12 cards, spaced repetition per `00-project/learning-roadmap.md` §0.4 (`Rev` intervals). Each card names the misconception it's designed to catch, not just the fact.

---

**1. Q: What is a port, in hexagonal architecture?**
A: An interface *owned by the domain*, stating what it needs or offers. Misconception it catches: confusing a port with its implementation — the port is the interface, not the adapter.

**2. Q: What is an adapter?**
A: A concrete implementation of a port, living in infrastructure — e.g. `PostgresOrderRepository implements OrderRepository`.

**3. Q: State the Dependency Rule.**
A: Source-code dependencies point only inward, toward the domain. Nothing inside knows anything about what's outside.

**4. Q: Where does a repository interface live?**
A: In the domain package, not next to its implementation. Misconception it catches: assuming "interface near implementation" is always good Java convention — here it inverts the pattern's whole point.

**5. Q: Cost of hexagonal architecture — name it honestly.**
A: Extra interfaces and, usually, mapping code between domain and persistence models. There is no free version of this pattern.

**6. Q: When should you NOT use hexagonal architecture?**
A: A thin CRUD service with no real business rules — there's no domain logic to protect, so the boundary is drawn around nothing.

**7. Q: B+Tree lookup path — one sentence.**
A: Root → internal nodes (routing keys) → leaf node holding either the row or a heap-tuple pointer, in `O(log n)` comparisons.

**8. Q: Leftmost-prefix rule, precisely.**
A: An index on `(A, B)` serves queries filtering `A` alone or `A` and `B` together, but not `B` alone.

**9. Q: What proves an index-only scan happened, in `EXPLAIN` output?**
A: `Index Only Scan` as the node type, and `Heap Fetches: 0`.

**10. Q: Seq-scan-wins condition — name the mechanism, not a percentage.**
A: When the query matches a large enough fraction of the table that random-I/O heap fetches via the index cost more than one sequential read of the whole table.

**11. Q: Clustered vs non-clustered index, by engine.**
A: InnoDB: primary key IS the clustered index, rows stored in PK order. PostgreSQL: no clustered-index concept — every table is a heap, every index is secondary.

**12. Q: The LRU cache bug — name the exact missing operation.**
A: `map.remove(key)` when unlinking an *existing* key's node, before the capacity check — without it, the stale map entry keeps `map.size()` at capacity during a pure update, triggering an incorrect eviction of the true LRU entry.
