# Coupling, Cohesion, and Code Smells — Real Demo

Backs [`syllabus/04-software-design/coupling-cohesion-and-code-smells.md`](../../../syllabus/04-software-design/coupling-cohesion-and-code-smells.md) (T-1703).

Pure JDK, no dependencies. Tested on OpenJDK 21.0.12.

## Run it

```bash
./run.sh
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

### 1. God Class coupling, measured, not asserted (`coupling/CouplingMeasurementDemo.java`)

`GodOrderProcessor` collaborates with 5 distinct external types (`TaxTable`, `ShippingRateTable`,
`InventoryStore`, `EmailGateway`, `PaymentGateway`) — measured via reflection (distinct declared
field types outside `java.*`), not eyeballed. Decomposed into six single-purpose classes, the
**largest individual class's coupling drops to 1** — a real 5:1 reduction, per class.

**Honest finding, not hidden:** the new `OrderProcessor` orchestrator's own coupling count is
actually **6, higher than the original God Class's 5** — decomposition doesn't make coupling
vanish system-wide, it redistributes it: something still has to coordinate six collaborators.
What genuinely improved is that each *individual* piece is now simple, single-purpose, and
independently testable — the orchestrator's higher raw count is the real, acceptable cost of
that, not a flaw in the measurement.

**Feature Envy, fixed and verified byte-for-byte identical:** `loyaltyDiscountPercent(Customer)`
lived on `GodOrderProcessor` but read only `Customer`'s own fields — moved onto `Customer`
itself. Both versions run the identical 3 real orders and produce **exactly matching totals**
(`Double.compare(...) == 0`), the same before/after test-parity technique
[`refactoring-discipline.md`](../../../syllabus/18-engineering-practices/refactoring-discipline.md)'s
own practice demo uses — proof the refactor changed structure, not behavior.

### 2. Law of Demeter: a real, measured fragility difference (`demeter/v1/`, `demeter/v2/`)

Two client styles, both calling into an identical `Customer`/`Wallet`/`Card` object graph in v1
and producing byte-for-byte identical output:

- **Train-wreck style** (`TrainWreckReceiptPrinter`, `TrainWreckRefundService`,
  `TrainWreckOrderConfirmationEmailer`): `customer.getWallet().getCard().getLast4Digits()`.
- **Demeter-compliant style** (`DemeterReceiptPrinter`, `DemeterRefundService`,
  `DemeterOrderConfirmationEmailer`): `customer.getCardLast4Digits()`, delegating through `Customer`.

`v2/` makes one real internal-structure change: `Wallet` now holds a `List<Card>` instead of a
single `Card`, and its old `getCard()` accessor is gone, replaced by `getPrimaryCard()` returning
`Optional<Card>`. `Customer.getCardLast4Digits()`'s **public signature is unchanged** — only its
internal implementation adapts.

The 6 client files are copied into `v2/` **completely unchanged** (only the package line differs).
Compiling them against the new `Wallet`/`Customer`:

- **All 3 Demeter-compliant clients compile successfully, unmodified.**
- **All 3 train-wreck clients fail with a real compiler error each** — `cannot find symbol: method
  getCard()` — exactly one broken call site per class that reached through `Wallet` directly.

This is the concrete, measured version of "the Law of Demeter reduces the blast radius of an
internal-structure change": the identical real change to `Wallet` broke 3 of 6 client files, and
the specific 3 that broke are exactly the ones that violated the principle.
