# Architecture Decision Records (T-916) — worked examples and a real completeness check

Backing [`syllabus/17-architecture/architecture-decision-records.md`](../../../syllabus/17-architecture/architecture-decision-records.md)
(T-916). Three fully-worked, representative example ADRs — each grounded in real,
already-executed evidence from other chapters in this repository, not invented
numbers — plus a real, tested script that checks any ADR for completeness against
Michael Nygard's original four-section pattern.

**These are labeled, representative scenarios, not records of real decisions made by
a real company**, per this repository's standard for fictionalized examples. What is
real: every measurement, error message, and result each ADR cites — pulled directly
from this repository's own [CQRS](../../java/architecture/cqrs-read-write-separation/README.md),
[multi-region DR](../../sql/multi-region-failover-and-dr/README.md), and
[Schema Registry](../../java/kafka/schema-registry-and-compatibility-evolution/README.md)
practice code and their real, captured output.

## Files

- `adr-001-cqrs-for-order-reporting.md` — cites the real 4.6–5.4x query speedup and
  real eventual-consistency lag measurements from the CQRS chapter.
- `adr-002-streaming-replication-for-dr.md` — cites the real 0-rows-lost /
  10-of-10-rows-lost RPO contrast and real 0.98s RTO from the multi-region DR chapter.
- `adr-003-backward-compatibility-for-orders-topic.md` — cites the real 2×2
  compatibility matrix (real HTTP 200/409 results) from the Schema Registry chapter.
- `bad-example-missing-consequences.md` — deliberately incomplete, exists only to
  give the checker script a real, guaranteed-failing input.

## Run the real completeness check

```bash
python3 scripts/check_adr_completeness.py \
  practice/architecture/adr-examples/adr-001-cqrs-for-order-reporting.md \
  practice/architecture/adr-examples/adr-002-streaming-replication-for-dr.md \
  practice/architecture/adr-examples/adr-003-backward-compatibility-for-orders-topic.md
```

**Real observed output:**

```
  PASS   practice/architecture/adr-examples/adr-001-cqrs-for-order-reporting.md
  PASS   practice/architecture/adr-examples/adr-002-streaming-replication-for-dr.md
  PASS   practice/architecture/adr-examples/adr-003-backward-compatibility-for-orders-topic.md
```

Exit code: `0`.

Against the deliberately incomplete file:

```bash
python3 scripts/check_adr_completeness.py practice/architecture/adr-examples/bad-example-missing-consequences.md
```

**Real observed output:**

```
  FAIL   practice/architecture/adr-examples/bad-example-missing-consequences.md: missing Consequences
```

Exit code: `1`. The script genuinely parses `##` headings and genuinely fails on a
real, deliberately introduced defect — it isn't a script that always prints PASS.

## What this does and does not prove

The three worked ADRs' *scenarios* are representative, not real company history — but
every number, error message, and HTTP status they cite is real, previously-executed
evidence from this repository's own practice code, cross-linked directly rather than
restated from memory. The completeness checker only verifies structural completeness
(the four required headings are present) — it cannot and does not verify that an
ADR's *content* is any good, which is a real, honest limitation, not an oversight.
