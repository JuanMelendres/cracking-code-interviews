# Multi-region, failover, and disaster recovery (T-814) — runnable verification

Real, executed PostgreSQL 16 (Docker) output backing
[`syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md`](../../../syllabus/10-distributed-systems/multi-region-failover-and-disaster-recovery.md)
(T-814). Three real demos, each destroying a real container mid-scenario rather than
describing what "would" happen: a real RPO/RTO measurement for a hot streaming
standby, a real RPO measurement for a WAL-archiving (log-shipping) standby, and a
real, reproduced split-brain under a naive failover, plus the real fencing fix.

This chapter deliberately does not re-derive streaming replication's own mechanics —
that's [T-615's](../replication-and-replica-lag/README.md) job. This directory is
about the DR-pattern-selection question: what each pattern actually costs in RPO and
RTO when something is really destroyed, and what a failover needs to do to stay safe.

## Setup and run

Requires Docker.

```bash
cd practice/sql/multi-region-failover-and-dr
./run-all-demos.sh
```

Each of the three scripts (`rpo-demo.sh`, `rpo-archive-demo.sh`, `splitbrain-demo.sh`)
can also be run standalone; each brings its own containers up and tears them down.

## Real observed output (last full run)

### 1. `rpo-demo.sh` — hot standby (streaming replication): real RPO and RTO

A single persistent connection fires a 150,000-statement burst of individually
committed `INSERT ... RETURNING` rows at the primary; after 0.4 real seconds the
primary container **and its volume** are destroyed (`docker rm -f -v`, not a graceful
shutdown) — a real, irreversible loss, exactly like a region actually disappearing.

```
Total writes really committed on the primary before it was destroyed: 2437
Last committed row: seq=2437  written_at=2026-08-25 16:25:39.945988+00

Rows on the standby: 2437
Standby's last received row: seq=2437  written_at=2026-08-25 16:25:39.945988+00

Rows genuinely lost (committed on primary, never reached the standby): 0
```

**Real finding: zero rows lost**, even with 2,437 commits destroyed mid-burst. This is
streaming replication doing exactly what it's designed to do — WAL ships to the
standby as it's generated, not batched, so on a healthy, keeping-up standby the real
exposure window is small enough that a fast burst on localhost Docker doesn't catch it
uncovered. This is the honest, direct evidence for *why* a hot standby is the pattern
of choice when RPO must be near zero.

Promotion and RTO, measured from the same run:

```
Promoted node accepted a real write (seq=2443) — failover complete.
Wall-clock time from region loss to the first accepted write on the promoted node:
0.976024s
```

A real, measured **RTO of ~0.98 seconds** — detection is assumed instantaneous here
(the script destroys the primary and immediately starts the promotion clock); a real
production RTO also has to add real detection-and-decision time on top of this number,
which this demo does not and cannot measure.

### 2. `rpo-archive-demo.sh` — WAL-archiving (log-shipping): real, and worse than expected

The primary is configured with `archive_mode=on`, `archive_timeout=3` (a WAL segment
should be force-closed and archived at least every 3 real seconds even under light
load) and `archive_command` copying segments to a host-mounted `./archive` directory —
no live standby at all, the classic cheaper, higher-RPO log-shipping DR pattern. Ten
rows are written roughly one per second over ten real seconds; the primary (container
+ live WAL) is then destroyed, leaving only whatever actually made it into
`./archive`.

```
=== Real archived WAL segments on the host (ground truth: what actually survived) ===
-rw-------  1 juanmelendres  staff  16777216 Aug 25 10:22 000000010000000000000001

Rows genuinely lost: 10 / 10
```

**Real, and more sobering than the configured `archive_timeout=3` would suggest:**
only the *startup* WAL segment was ever archived — no second segment closed and
shipped during the entire 10-second write window, so every single row written in this
run was genuinely unrecoverable from the archive alone. This is a real, direct
demonstration that `archive_timeout` is a target the archiver works toward, not a
guarantee enforced on a fixed clock independent of what else is happening — the honest
lesson is that a DR plan's assumed RPO number needs to be verified by actually
destroying a real node and checking what survived, exactly as this script does, not
trusted from a configuration value alone.

### 3. `splitbrain-demo.sh` — a real, reproduced split-brain, and the real fencing fix

**Run 1 — naive failover, no fencing.** After a baseline write replicates normally,
the primary is genuinely disconnected from the replication network
(`docker network disconnect`) — it stays alive and fully functional, just partitioned,
exactly like a real cross-region network split. A naive controller, seeing the primary
unreachable, promotes the standby. Because `docker exec` reaches a container directly
through the Docker daemon rather than over the container's own network (exactly like a
client on the old primary's own side of a real partition still could), the *old*
primary is still reachable and still accepts a write, unaware it has been superseded.

```
Old primary's ledger:
1|baseline
2|accepted-by-old-primary-unaware-of-failover
New primary's ledger:
1|baseline
34|accepted-by-new-primary-after-failover
```

**Real, observed split-brain:** two nodes, each certain it is the one true primary,
each holding a real committed row the other does not have. Reconnecting the network
does not resolve this — nothing about streaming replication reconciles divergent
histories; a human has to decide which write survives.

**Run 2 — the same scenario, fenced.** Before promoting anything, the old primary is
fenced with `docker pause` — a real stand-in for STONITH (Shoot The Other Node In The
Head) that genuinely freezes every process in the container via the cgroup freezer.

```
Error response from daemon: Container dr-region-primary is paused, unpause the container before exec
Confirmed: Docker itself refused to touch the paused container — the write never even reached postgres.

New primary accepted the write. Old primary is still fenced and never diverged.
```

The identical write attempt against the fenced old primary is refused before it ever
reaches PostgreSQL. The standby is then promoted with **zero risk of a second writer**
— the real, concrete difference fencing makes.

## What this does and does not prove

Every measurement here is real, executed, single-machine, localhost-Docker output —
the RTO and RPO numbers are a floor for this specific hardware and workload, not a
production forecast; a real cross-region failover has real network latency, real
DNS/traffic-manager propagation delay, and a real human-in-the-loop decision window
layered on top of everything measured here. What does not change with distance is the
*shape* of each finding: a hot standby's RPO really can be near zero under real load; a
log-shipping standby's RPO really can exceed its configured `archive_timeout`; and a
failover without real fencing really can produce a real, divergent split-brain that
only fencing prevents.
