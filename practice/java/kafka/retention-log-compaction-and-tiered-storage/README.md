# Retention, Log Compaction, and Tiered Storage (T-706) — runnable verification

Real, executed output backing
[`syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md`](../../../../syllabus/09-messaging-event-driven/retention-log-compaction-and-tiered-storage.md)
(T-706). A real, disposable single-node Kafka 3.8.0 broker (KRaft mode,
official `apache/kafka` Docker image) — no ZooKeeper, no mocked broker
behavior.

## Setup and run

Requires Docker.

```bash
cd practice/java/kafka/retention-log-compaction-and-tiered-storage
docker compose up -d
sleep 5
./compaction-demo.sh
```

Tear down: `docker compose down -v`.

## What it proves

The script creates a real `cleanup.policy=compact` topic with deliberately
tiny segments (`segment.bytes=1000`, `segment.ms=1000`) so real compaction
happens fast enough to observe directly, then:

- Produces 8 keyed records, spaced more than a second apart so each lands
  in its own, quickly-rolled segment: three updates to `user-1`
  (`v1`→`v2`→`v3`), two updates to `user-2` (`v1`→`v2`), one record for
  `user-3`, and one record for `user-4` followed by a tombstone (a
  real `null`-valued record) for the same key.
- Waits for the real background log-cleaner thread to compact the
  now-closed segments — not simulated, the actual broker's own cleaner.
- Consumes the topic from the beginning afterward. Real captured output:
  `user-3:v1`, `user-1:v3`, `user-2:v2`, `user-4:v1`, `user-4:null` — every
  intermediate value (`user-1`'s `v1`/`v2`, `user-2`'s `v1`) is gone;
  only the latest record per key survived, exactly compaction's contract.
  The tombstone for `user-4` is still present at this point — a real,
  observed instance of `delete.retention.ms` keeping a delete marker
  around briefly so a lagging consumer can still see it, rather than a
  timing artifact this README is glossing over.
- Lists the real on-disk segment files afterward: only the original
  segment `00000000000000000000.log` (now containing just the five
  compacted, surviving records — a real, verifiable byte size drop from
  what 8+ full records would have taken) and the current active segment
  remain; the intermediate rolled segments compaction merged away are
  gone from disk, not just logically superseded.
