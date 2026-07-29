# Domain Purity Exercise

**Deliverable for T-901 · Friday's `domain-purity.md`**

Take one real aggregate from a system you know — your own system, ideally, or a well-known open-source one if that's unavailable — and document its current framework coupling, the port/adapter refactor, what it costs, and whether you would actually do it.

---

## 1. Template (fill this from your own system)

### (a) Current framework coupling

List every framework/infrastructure type the aggregate's "domain" class touches directly today:

```
Class: __________
Framework types imported: __________
  e.g. @Entity, @Id, @OneToMany (JPA)
  e.g. extends CrudRepository (Spring Data)
  e.g. ObjectMapper, @JsonProperty (Jackson)
```

### (b) The port/adapter refactor

```
Port interface (owned by the domain package):
  interface __________Repository {
      __________
  }

Adapter (implements the port, lives in infrastructure package):
  class Postgres__________Repository implements __________Repository {
      __________
  }
```

### (c) What breaks

List every call site that has to change, and estimate the size of the change honestly:

```
Call sites affected: __________
Mapping code added: __________
Tests that need rewriting vs. tests that get simpler: __________
```

### (d) Would you actually do it? — **the Senior signal**

A candidate who applies hexagonal architecture unconditionally has not understood the trade-off. Answer honestly:

```
Verdict: __________
Reasoning: __________
```

---

## 2. Worked example (fully filled, for calibration — not a substitute for your own)

**System:** a hypothetical order-management service. **Aggregate:** `Order`.

**(a) Current coupling:** `Order` is a JPA `@Entity` with `@OneToMany` to `OrderLine`, injected `EntityManager` calls scattered across `OrderService`, and a `@Transactional` annotation on the same method that calls out to a payment client — three separate concerns (persistence, transaction boundary, external I/O) mixed into one method.

**(b) Refactor:** extract `OrderRepository` (`save(Order)`, `findById(OrderId)`) as a port in `domain.order`. `PostgresOrderRepository implements OrderRepository` in `infrastructure.persistence`, holding all JPA-specific code — the `@Entity`-annotated class becomes a private, adapter-local `OrderEntity`, converted at the boundary.

**(c) What breaks:** every call site using `orderRepository.findById(...).getOrderLines()` still compiles unchanged if the port's return type stays `Order` — this is the actual payoff, most call sites don't move. What *does* change: the mapper class (`OrderEntity` ↔ `Order`, ~40 lines, new), and the handful of places that relied on JPA lazy-loading proxies triggering "for free" on access (those now need explicit, upfront loading in the adapter, since the domain object can't lazy-load anything).

**(d) Verdict:** **yes, for this aggregate specifically** — `Order` carries real business rules (validating line-item totals against a discount policy, enforcing state-transition rules for `PLACED → SHIPPED → DELIVERED`), so there's genuine domain logic worth protecting from infrastructure churn. The reasoning would flip for a `Notification` aggregate in the same system that's genuinely just `{id, message, sentAt}` with no business rule beyond "insert this row" — see the counter-case below.

## 3. The counter-case — when the answer is legitimately "no"

**Aggregate:** `Notification` (log of sent notifications, no business logic beyond persistence).

**Verdict: no.** There is no domain logic to protect — every "use case" reduces to `repository.save(mapper.toEntity(dto))` with no branching business rule anywhere. Introducing a port and adapter here adds an interface, an adapter class, and a mapper for a class that does nothing a direct `@Entity` + `CrudRepository` pairing doesn't already do just as correctly, at a fraction of the code. Point (d) exists precisely to catch this: applying the pattern here would be indirection with no corresponding payoff (see `01-clean-hexagonal-architecture.md` §4, "when NOT to use it").

**The lesson:** the same engineer, in the same system, correctly says "yes" for `Order` and "no" for `Notification`. That asymmetry — not blanket advocacy for the pattern — is what a Staff interviewer is listening for in Q5 of the interview-questions list.

## 4. Exit check

Your own `domain-purity.md`, produced from §1 against a real system, must include a genuine verdict in part (d) — not a default "yes, always apply hexagonal architecture." If your honest answer is "no" for the aggregate you picked, that is a complete, successful exercise; pick a second aggregate afterward where the answer is "yes," so you have both calibration points before Friday's recording.
