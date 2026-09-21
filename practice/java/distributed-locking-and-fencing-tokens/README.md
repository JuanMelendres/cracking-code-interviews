# Distributed Locking and Fencing Tokens — Real Demo

Backs [`syllabus/10-distributed-systems/distributed-locking-and-fencing-tokens.md`](../../../syllabus/10-distributed-systems/distributed-locking-and-fencing-tokens.md) (T-2422).

Pure JDK, no dependencies. A real, minimal lease-based lock service (the same shape as etcd's lease-based locks or a ZooKeeper ephemeral znode) and a real timing-driven reproduction of the classic stale-lock-holder bug Martin Kleppmann's ["How to do distributed locking"](https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html) made well known — plus the real fix.

## Run it

```bash
mkdir -p out
javac -d out src/demo/*.java
java -cp out demo.LockContentionDemo
java -cp out demo.UnsafeLockDemo
java -cp out demo.FencedLockDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

1. **`LockService` really does provide ordinary mutual exclusion** — `LockContentionDemo`: a second client's `acquire()` while the first client's lease is still genuinely valid is refused with a real `IllegalStateException`, not silently granted.

2. **A real 500ms pause longer than a real 300ms lease reproduces real data corruption — not a hypothetical.** `UnsafeLockDemo`: `Client-A` acquires a lock with a 300ms TTL, then pauses for 500ms (a real `Thread.sleep` standing in for a real GC pause, a slow disk write, or any other real stall). While paused, `Client-A`'s lease genuinely expires and `Client-B` legitimately acquires the lock and writes `"B-value"`. `Client-A` then wakes up — still believing it holds the lock, since it never re-checks — and writes `"A-value"` anyway. Real captured output: the final stored value is `"A-value"`, `Client-B`'s authoritative write silently overwritten. Reproduced consistently across repeated runs (the 200ms margin between the 300ms lease and 500ms pause makes this deterministic, not a rare race).

3. **The real fix: fencing tokens, enforced by the resource being written to, not by the lock holder's own good behavior.** `FencedLockDemo`: the identical real timeline, but every write now carries its lease's own real, monotonically increasing token. `Client-B`'s write (token 2) is accepted; `Client-A`'s stale write (token 1) is genuinely rejected by `FencedStorage` itself — `token 1 <= last accepted token 2` — because the storage layer, not the lock service, is what actually enforces staleness rejection. Real captured output: the final stored value is `"B-value"`, `Client-A`'s stale write explicitly rejected rather than silently applied.

## Why the storage layer, not the lock service, has to be the one enforcing this

A common, tempting-but-wrong fix is "make the lock service smarter" (shorter leases, faster expiry detection, a callback when a lease expires). None of these actually close the gap, because the fundamental problem is that `Client-A` cannot know, from inside its own pause, that time has passed at all — no amount of lock-service sophistication changes that a paused process cannot observe its own pause. The only mechanism that closes the gap completely is the resource being *written to* independently verifying the token on every write, which is exactly what `FencedStorage` does and `UnsafeStorage` doesn't.
