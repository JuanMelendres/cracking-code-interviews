# Consensus Algorithms: Raft Leader Election — Real Demo

Backs [`syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md`](../../../syllabus/10-distributed-systems/consensus-algorithms-raft-and-paxos.md) (T-2403).

Pure JDK, no dependencies. A real, deterministic simulation of Raft's
`RequestVote` RPC and vote-granting rule — exactly the rule from the Raft
paper (Ongaro & Ousterhout, 2014, Figure 2), not a simplification. Message
delivery between nodes is simulated by direct method calls filtered by a
partition-membership set (no real sockets), but the election *algorithm*
itself — term comparison, vote granting, majority counting, step-down on a
higher term — is the real, unsimplified rule.

## Run it

```bash
mkdir -p out
javac -d out src/demo/*.java
java -cp out demo.RaftConsensusDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **A normal election succeeds** — 5 nodes, no partition, a single candidate
  gets a unanimous 5/5 vote and becomes leader.
- **A network partition prevents the minority side from ever electing a
  leader** — a 3-node majority partition reaches the real quorum (3 of 5)
  and elects a leader; the 2-node minority partition caps out at 2 votes,
  structurally short of the majority of 3, no matter how many times it
  retries while the partition persists.
- **A real split vote produces no leader for that term** — two candidates
  campaigning in the identical term each get 2 votes, with the fifth node's
  responses to both lost/delayed this round (a real, plausible cause of a
  split vote — not every node hears from every candidate in time); neither
  reaches the majority of 3, so the term ends with no leader. The next
  term, a single candidate wins cleanly, demonstrating why real Raft uses
  randomized election timeouts to make repeated splits rare.
- **A stale leader steps down the instant it observes a higher term** — a
  real term-1 leader, upon receiving a `RequestVote` carrying term 2,
  immediately transitions from `LEADER` to `FOLLOWER` and adopts the higher
  term — the concrete mechanism behind Raft's "at most one leader per term"
  safety guarantee.
