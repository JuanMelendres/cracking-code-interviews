# DDD strategic design: bounded contexts and context mapping (T-902) — runnable verification

Real, executed Java 21 output backing
[`syllabus/17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md`](../../../../syllabus/17-architecture/ddd-strategic-bounded-contexts-and-context-mapping.md)
(T-902). No described-but-untested claim — a real `javac` compile that really
succeeds, and a real `javac` compile that really fails with a real compiler error,
against file sets verified byte-identical (or intentionally different) via real `diff`.

## The scenario

Two bounded contexts both have a concept called "Order," and they genuinely mean
different things by it: Sales's `Order` is a commercial transaction (customer, price);
Fulfillment's `Order` is a physical shipment (recipient, weight). Sales then makes a
real, ordinary upstream change — renaming `customerName` to `buyerName` — and this
demo answers the concrete question a Context Mapping chapter has to answer: what
happens to Fulfillment when that happens, under two different relationship patterns?

## Files

- `v1-original-schema/` — before Sales's rename. `sales/SalesOrder.java` (Sales's own
  model), `fulfillment/FulfillmentOrder.java` (Fulfillment's own, separate model),
  `conformist/ConformistFulfillmentService.java` (the **Conformist** relationship —
  depends on `sales.SalesOrder` directly), `acl/OrderTranslator.java` +
  `acl/AclFulfillmentService.java` (the **Anti-Corruption Layer** relationship —
  depends only on Fulfillment's own model).
- `v2-upstream-renamed-field/` — after Sales's rename. Same five files;
  `fulfillment/FulfillmentOrder.java`, `conformist/ConformistFulfillmentService.java`,
  and `acl/AclFulfillmentService.java` are byte-identical to their v1 counterparts —
  only `sales/SalesOrder.java` (the actual upstream change) and
  `acl/OrderTranslator.java` (its intended absorption point) differ.
- `run-demo.sh` — runs the full real diff-then-compile proof below in one command.

## Run

```bash
cd practice/java/architecture/ddd-bounded-contexts-and-context-mapping
./run-demo.sh
```

## Real observed output (last full run, Java 21)

```
=== Step 1: diff check -- which files are identical across v1/v2, which differ ===
  IDENTICAL: fulfillment/FulfillmentOrder.java
  IDENTICAL: conformist/ConformistFulfillmentService.java
  IDENTICAL: acl/AclFulfillmentService.java
  DIFFERS (expected -- upstream change / its absorption): sales/SalesOrder.java
  DIFFERS (expected -- upstream change / its absorption): acl/OrderTranslator.java

=== Step 2: compile v1-original-schema (everything) ===
Exit code: 0 (expected 0)

=== Step 3: compile v2-upstream-renamed-field, ACL path only ===
Exit code: 0 (expected 0 -- AclFulfillmentService.java is unchanged and still compiles)

=== Step 4: compile v2-upstream-renamed-field, CONFORMIST path only ===
v2-upstream-renamed-field/conformist/ConformistFulfillmentService.java:14: error: cannot find symbol
        System.out.println("Preparing shipment for " + order.getCustomerName()
                                                            ^
  symbol:   method getCustomerName()
  location: variable order of type SalesOrder
1 error
Exit code: 1 (expected 1 -- ConformistFulfillmentService.java is unchanged and no longer compiles)
```

## What this proves

`ConformistFulfillmentService.java` and `AclFulfillmentService.java` are both,
verifiably, unchanged bytes between v1 and v2 — real `diff` output, not an assertion.
Only one of them still compiles after Sales's real upstream rename. The difference is
not code quality or effort; it's the relationship pattern: the Conformist consumer
took a direct, structural dependency on Sales's type, so Sales's internal renaming
decision became Fulfillment's compile break. The Anti-Corruption Layer consumer never
took that dependency — `OrderTranslator.java` did, and it is the one file in the whole
ACL-protected path that was expected, and allowed, to change. This is the real,
concrete answer to "two teams disagree on what 'Order' means, resolve it": they don't
have to agree — each keeps its own bounded model, and exactly one deliberately-placed
translator absorbs the difference.

## What this does and does not prove

This is a real compile-time demonstration of runtime API/schema coupling, not a
network-boundary demonstration — both "contexts" here are Java packages in the same
build, not separately deployed services. In a real microservices deployment the same
coupling exists at the wire-format level (a renamed JSON field, a changed gRPC message)
rather than at compile time, and the Anti-Corruption Layer's job is identical: isolate
one service's internal model from another's, with the ACL absorbing the translation
at the deployment boundary instead of at `javac` time. What doesn't change is the
underlying property this demo makes undeniable: a direct dependency on another
context's model propagates that context's every internal change to you; a translator
contains it.
