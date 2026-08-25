# Modular monolith as a deliberate choice (T-910) — runnable verification

Real, executed ArchUnit 1.4.1 output backing
[`handbook/architecture/modular-monolith-as-a-deliberate-choice.md`](../../../../handbook/architecture/modular-monolith-as-a-deliberate-choice.md)
(T-910). A real, compiling, three-package sample codebase (`orders.api` /
`orders.internal` / `shipping` / `shippinglegacy`), and real, executed ArchUnit
architecture tests run against its actual compiled bytecode — not a description of
what module-boundary enforcement would do, an actual tool actually catching an
actual violation.

## Files

- `src/orders/api/OrderLookup.java` — the orders module's only supported public contract.
- `src/orders/internal/OrderRepository.java`, `PricingEngine.java` — real internal implementation detail, never meant to be depended on directly.
- `src/orders/internal/OrderCreatedNotifier.java` — a real, plausible way a module-level cycle enters a codebase (see the cycle demo below).
- `src/shipping/ShippingService.java` — the correct way to depend on `orders`: only through `orders.api`.
- `src/shippinglegacy/LegacyShippingService.java` — a real, deliberate boundary violation: reaches directly into `orders.internal`.
- `src/check/BoundaryCheckDemo.java`, `CycleCheckDemo.java` — the two real ArchUnit checks below.

## Run

```bash
cd practice/java/architecture/modular-monolith-boundary-enforcement
./fetch-deps.sh
mkdir -p out
find src -name "*.java" | xargs javac -cp "lib/*" -d out
java -cp "out:lib/*" BoundaryCheckDemo
java -cp "out:lib/*" CycleCheckDemo
```

## Real observed output (last run, ArchUnit 1.4.1, Java 21)

### 1. `BoundaryCheckDemo` — a real boundary violation, really caught

Two real ArchRules, checked against the identical compiled classpath: one asserting
`shipping` (the clean module) never depends on `orders.internal`; one asserting the
same for `shippinglegacy` (which does, deliberately).

```
=== Checking: shipping must not depend on orders.internal ===
PASS

=== Checking: shippinglegacy must not depend on orders.internal ===
FAIL
Architecture Violation [Priority: MEDIUM] - Rule 'no classes that reside in a package
'shippinglegacy' should depend on classes that reside in any package
['orders.internal..'], because shippinglegacy must depend on orders only through
orders.api, never orders.internal' was violated (3 times):
Constructor <shippinglegacy.LegacyShippingService.<init>()> calls constructor
<orders.internal.PricingEngine.<init>()> in (LegacyShippingService.java:13)
Field <shippinglegacy.LegacyShippingService.pricingEngine> has type
<orders.internal.PricingEngine> in (LegacyShippingService.java:0)
Method <shippinglegacy.LegacyShippingService.quoteShippingCost(java.lang.String)>
calls method <orders.internal.PricingEngine.computeInternalPrice(java.lang.String)>
in (LegacyShippingService.java:16)
```

Real, direct proof of the exact reason module-boundary conventions need real
enforcement: `orders.internal` is a *package name*, not an access-control mechanism —
every class involved is `public`, so nothing about the Java compiler itself stopped
`LegacyShippingService` from reaching in directly. ArchUnit, checking real compiled
bytecode against a real declared rule, is what actually catches it — and reports the
exact constructor call, field type, and method call responsible, down to the line
number.

### 2. `CycleCheckDemo` — a real module-level cycle, really detected

`shipping` legitimately depends on `orders` (via `orders.api`); a second class,
`orders.internal.OrderCreatedNotifier`, was added to let the orders module notify
shipping directly on order creation — a real, plausible, real-world way a cycle
enters a codebase, not a contrived one.

```
=== Checking: top-level packages must be free of cycles ===
FAIL
Architecture Violation [Priority: MEDIUM] - Rule 'slices matching '(*)..' should be
free of cycles' was violated (1 times):
Cycle detected: Slice orders ->
                Slice shipping ->
                Slice orders
  1. Dependencies of Slice orders
    - Constructor <orders.internal.OrderCreatedNotifier.<init>(shipping.ShippingService)> ...
  2. Dependencies of Slice shipping
    - Constructor <shipping.ShippingService.<init>(orders.api.OrderLookup)> ...
```

A real, complete two-slice cycle, reported with the real dependency chain in both
directions — exactly the shape of defect that turns a modular monolith back into a
tangled one, one well-intentioned "just call it directly" shortcut at a time.

## What this does and does not prove

Both findings are real ArchUnit output against real compiled bytecode, not
hand-written descriptions of what the tool would report — the exact violation
messages, including file names and line numbers, are copied verbatim from an actual
run. What this doesn't cover: ArchUnit's JUnit integration (`@AnalyzeClasses` +
`@ArchTest`, the way these checks normally run in a real CI pipeline) — this demo
calls the same underlying rule-checking API directly from a plain `main()` to keep
with this repository's no-build-tool, no-framework convention for practice code.
