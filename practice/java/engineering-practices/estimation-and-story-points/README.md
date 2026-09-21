# Estimation and Story Points: Velocity Comparison — Real Demo

Backs [`syllabus/18-engineering-practices/estimation-and-story-points.md`](../../../../syllabus/18-engineering-practices/estimation-and-story-points.md) (T-1805).

Pure JDK, no dependencies. Fully deterministic — no timing, no
randomness — every run produces byte-identical output; re-run confirmed
identical to [`output-transcript.txt`](output-transcript.txt).

## Run it

```bash
mkdir -p out
javac -d out src/*.java
java -cp out VelocityComparisonDemo
```

## What it proves

Two teams deliver the **exact same 8 tasks**, representing the **exact same
135 hours** of real, ground-truth engineering effort in a sprint — nothing
about the actual work delivered differs between them. Each team estimates
those same 8 tasks in story points using its own calibration (`FibonacciScale.nearest`
snaps a raw estimate to the standard 1/2/3/5/8/13/20/40/100 planning-poker
scale):

- **Team A** calibrated roughly 1 point per 2 hours (their own reference
  story, from whenever their team formed).
- **Team B** calibrated roughly 1 point per 1 hour (a different reference
  story).

Real captured output: Team A's velocity for this sprint is **61 points**;
Team B's velocity for the **identical work** is **127 points** — a real
**2.1x** difference, entirely explained by calibration, with zero
difference in actual hours delivered (135 for both). A manager comparing
these two velocity numbers directly, without knowing this, would wrongly
conclude Team B is faster or more productive.

## Honest limitations

- The two teams' calibration functions (`hours / 2.0` and `hours / 1.0`)
  are simplified, deterministic stand-ins for what a real team's
  historical reference-story calibration looks like — the real world adds
  noise (different task complexity a fixed hours number doesn't capture,
  team-specific risk buffers), but the core mechanism this demo proves
  (two independently-calibrated point scales are not the same unit) holds
  regardless of that noise.
- The task list and hour values are illustrative, chosen to span the
  Fibonacci scale's range clearly — the proof is about the calibration
  mechanism, not these specific numbers.
