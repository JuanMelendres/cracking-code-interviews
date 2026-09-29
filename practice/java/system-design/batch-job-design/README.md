# Batch and Scheduled Job Design — Real, Executed Demo

Backs [Batch and Scheduled Job Design](../../../../syllabus/11-system-design/batch-and-scheduled-job-design.md) (T-2443).

Runs against a real **H2 2.3.232** in-memory database on OpenJDK 21.0.12. Every lock, transaction, rollback, and checkpoint below is genuine SQL — the conditional `UPDATE` used as a job lock and the checkpoint committed alongside its chunk are the actual mechanisms a production job uses, not simulations of them. Nothing is left behind; the database lives only for the run.

## Run it

```bash
./fetch-deps.sh
mkdir -p out
javac -cp "lib/*" -d out src/*.java
java -cp "out:lib/*" BatchJobDemo
```

Full output in `output-transcript.txt`.

## 1. The same `@Scheduled` job deployed on three instances

Three threads stand in for three pods, released simultaneously because their cron expressions are identical. Correctness is measured by counting rows in a side-effect table, not by trusting the job's own reporting.

```text
Unguarded (@Scheduled on every instance) invoices=1000  side_effect rows=3000  workers that ran=3  -> EVERY INVOICE PROCESSED 3x
Guarded by a database lock               invoices=1000  side_effect rows=1000  workers that ran=1  -> correct
```

Every invoice was processed **three times**. If that job sends an email, charges a card, or posts a ledger entry, it did so three times, and nothing failed or logged an error. This is the single most common way a correct-looking scheduled job breaks the moment a service scales past one replica.

The fix is one statement:

```sql
UPDATE job_lock SET held_by = ?, locked_at = CURRENT_TIMESTAMP
 WHERE name = ? AND held_by IS NULL
```

It either matches the free row and returns 1, or matches nothing and returns 0. The database's own row-level locking makes it atomic across every instance, so a job that runs once a night needs no coordination service — the database you already have is sufficient.

## 2. A crash halfway through, with and without a checkpoint

A 1,000-invoice job crashes at invoice 600, then restarts.

```text
No checkpoint  : attempt 1 inserted 599 rows, then crashed at invoice 600
                 committed after attempt 1: 0  (the whole transaction rolled back)
                 attempt 2 restarted from invoice 1 and committed 1000
                 result: correct, but 1599 units of work were performed to commit 1000

With checkpoint: attempt 1 inserted 599 rows, then crashed at invoice 600
                 committed after attempt 1: 500  (only the open chunk rolled back)
                 checkpoint = 500, so attempt 2 resumed at invoice 501
                 total committed = 1000 across 1000 distinct invoices  -> every invoice processed exactly once
                 work performed to commit 1000: 1099 units (99 wasted)
```

Both end **correct** — and that is the point worth being precise about. The single-transaction version is not wrong; it is wasteful and operationally fragile. It performed **1,599** units of work to commit 1,000, and a six-hour job that fails in hour five redoes all five hours. It also holds one transaction open for the entire run, with the lock and undo-log consequences that implies.

The checkpointed version wasted exactly **99** units — the open chunk — because progress was recorded durably as it went.

The detail that makes it work: the checkpoint is written **in the same transaction** as the chunk it describes. If they committed separately, a crash between the two would either lose work or skip it.

## 3. Chunk size: throughput versus work lost to a crash

Same 10,000 rows, varying only the commit interval:

| chunk size (rows/commit) | duration | worst-case rows lost to a crash |
|---|---|---|
| 1 | 148 ms | 0 |
| 10 | 41 ms | 9 |
| 100 | 30 ms | 99 |
| 1,000 | 21 ms | 999 |
| 10,000 | 16 ms | 9,999 |

Commit cost is per-chunk, so bigger chunks are faster — **9.25x** from end to end here. But the work at risk in a crash is exactly one chunk, by construction. Chunk size is therefore a durability budget, not a performance setting: the question is not "how fast can this go" but "how much repeated work is acceptable after a crash."

## 4. One poison record: fail-fast versus skip

A single unprocessable record at position 500 of 1,000:

```text
fail-fast : stopped at invoice 500. processed=499  skipped=0  remaining=500  -> job must be fixed and rerun
skip      : ran to completion.     processed=999  skipped=1  remaining=0    -> job "succeeded"; the skip is only visible if it is counted
```

Neither is correct in general; they fail differently. Fail-fast leaves 500 invoices unprocessed and is loud about it. Skip finishes and reports success, and the one dropped record exists only as a counter — if nothing reads that counter, the failure is invisible forever.

The practical rule the numbers support: a skip policy is only safe when the skip count is itself an alerted metric and skipped records land somewhere they can be inspected and replayed. Otherwise "the job succeeded" is a claim nobody has checked.
