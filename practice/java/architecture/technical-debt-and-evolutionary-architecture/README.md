# Technical debt and evolutionary architecture (T-913) — runnable verification

Real, executed Java 21 output backing
[`handbook/architecture/technical-debt-and-evolutionary-architecture.md`](../../../../handbook/architecture/technical-debt-and-evolutionary-architecture.md)
(T-913). No described-but-untested claim about "coupling got worse" — a real
reflection-based fitness function measuring a real, exact number against a real
class, both before and after a real incremental refactor.

## The scenario

`before.OrderProcessor` is what a real checkout flow looks like after ten separate,
individually-reasonable pull requests each added "just one more collaborator" — fraud
check, loyalty points, analytics — none of which looked like a problem in isolation.
`after.OrderProcessor` is the same class after three small, incremental extraction
steps (Pricing, Fulfillment, Compliance coordinators), with identical behavior.

## Files

- `before/` — ten collaborator classes and `OrderProcessor`, coupled directly to all
  ten.
- `after/` — the same ten collaborators, plus `PricingCoordinator`,
  `FulfillmentCoordinator`, `ComplianceCoordinator`, and a refactored `OrderProcessor`
  coupled to only four things.
- `CouplingFitnessFunction.java` — a real, minimal fitness function: reflection-based
  efferent-coupling measurement against a configurable threshold, no third-party
  library required.

## Run

```bash
cd practice/java/architecture/technical-debt-and-evolutionary-architecture
mkdir -p out
javac -d out before/*.java after/*.java CouplingFitnessFunction.java
java -cp out CouplingFitnessFunction
```

## Real observed output (last full run, Java 21)

```
=== Running coupling fitness function against before.OrderProcessor ===
[Fitness Function] before.OrderProcessor: efferent coupling = 10 (threshold: <= 5) -> FAIL

=== Running coupling fitness function against after.OrderProcessor ===
[Fitness Function] after.OrderProcessor: efferent coupling = 4 (threshold: <= 5) -> PASS

Build gate result: before=FAIL, after=PASS
A CI pipeline wiring this fitness function into the build would have REJECTED before.OrderProcessor at merge time, not discovered its coupling informally during an unrelated incident months later.
```

Both versions were also run directly and produce the identical final price
($205.20...) and the identical set of side effects (inventory reserved, payment
charged, shipping dispatched, notification sent, loyalty awarded, analytics tracked,
audit logged) — the refactor preserved behavior exactly; only the *number of things
`OrderProcessor` itself has to know about* changed, from 10 to 4.

## What this proves

The fitness function is real and automatable: it's an exact, reproducible number
(10, then 4), not a subjective code-review judgment call about whether a class "feels"
too coupled. Wired into a real CI pipeline as a build gate, this exact check would
have failed the pull request that pushed `OrderProcessor` past the threshold, at the
moment it happened — which is the actual mechanism evolutionary architecture uses to
keep an important architectural characteristic from silently degrading over dozens of
individually-reasonable changes, none of which looked risky on its own.

## What this does and does not prove

Efferent coupling by field count is one real, simple, legitimate fitness function —
not the only one, and not sufficient on its own to characterize "good architecture."
Real production fitness functions also check cyclomatic complexity, build time,
dependency-direction rules (see
[Modular Monolith as a Deliberate Choice](../../../../handbook/architecture/modular-monolith-as-a-deliberate-choice.md)'s
ArchUnit-based demos for that specific kind of check), test coverage trends, and
security-scan results. What transfers directly from this simple example is the
underlying mechanism: an automated, numeric, continuously-re-run check catches
architectural drift at the moment it's introduced, when a code reviewer's subjective
judgment might reasonably let each individual step through.
