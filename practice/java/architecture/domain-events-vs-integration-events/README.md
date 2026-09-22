# Domain Events vs. Integration Events — Real Demo

Backs [`syllabus/17-architecture/domain-events-vs-integration-events.md`](../../../../syllabus/17-architecture/domain-events-vs-integration-events.md) (T-2426).

Pure JDK, no dependencies. Fully deterministic — no timing, no
randomness, no threading — so every run produces byte-identical output;
re-run confirmed identical to [`output-transcript.txt`](output-transcript.txt).

## Run it

```bash
mkdir -p out
javac -d out src/*.java
java -cp out DomainVsIntegrationEventDemo
```

## What it proves

1. **Unsafe: publishing a domain event directly as the wire message.**
   `OrderCompletedDomainEventV1.publishDirectlyAsWireMessage()` puts the
   domain event's own internal fields straight onto the wire. A real
   downstream `NotificationConsumer` reads the `"total"` field and works
   fine — until a real, well-motivated internal refactor happens
   (`double total` → `BigDecimal grandTotal`, plus a new internal-only
   `loyaltyPointsEarned` field). The SAME consumer, asking for the SAME
   field it always asked for, now genuinely breaks: real captured output,
   `BROKEN, exactly as expected: Consumer expected field "total" but it
   was not present`.
2. **Safe: translating through a stable `OrderCompletedIntegrationEvent`
   contract.** `IntegrationEventTranslator` maps both the pre-refactor
   (`V1`) and post-refactor (`V2`) domain event shapes onto the identical
   public contract shape (`orderId`, `totalAmount`, `customerId`). The
   same consumer, reading `"totalAmount"` both times, gets the identical
   result before and after the internal refactor — real captured output:
   `49.99` both times — because the translation boundary absorbed the
   internal change instead of leaking it onto the wire.

## Honest limitations

- The "wire message" here is an in-process `Map<String, Object>`, not
  literal JSON bytes over a real message broker — the mechanism being
  proven (field presence/absence after an internal refactor) is identical
  either way; a real broker would just add real serialization on top of
  the same underlying fact.
- The refactor scenario (`double` → `BigDecimal`, a renamed field) is one
  concrete, realistic example of an internal-only change — the same
  breakage occurs for any internal reshaping (splitting a field, renaming
  for clarity, restructuring nested data) published without a translation
  boundary.
