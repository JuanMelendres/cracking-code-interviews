# Horizontal Pod Autoscaling: Mechanics, Metrics, and Scaling Behavior — Real Demo

Backs [`syllabus/14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md`](../../../../syllabus/14-devops-containers/horizontal-pod-autoscaling-mechanics-and-scaling-behavior.md) (T-2424).

Pure JDK, no dependencies. No mocked clock -- real `System.currentTimeMillis()`
and real `Thread.sleep()` between metric samples at ~300ms.

## Run it

```bash
mkdir -p out
javac -d out src/*.java
java -cp out HpaDemo
```

Real captured output in [`output-transcript.txt`](output-transcript.txt),
re-run twice to confirm reliability — only real-clock timing jitter (a few
ms) differs between runs; every replica-count decision is identical.

## What it proves

`HpaCalculator` and `HpaController` implement the real, documented
Kubernetes HPA algorithm (`desiredReplicas = ceil[currentReplicas *
(currentMetricValue / desiredMetricValue)]`, a 10% default tolerance band,
and the real scale-up-immediate/scale-down-stabilized asymmetry), applied
to the exact `target: 70%, min: 3, max: 12` HorizontalPodAutoscaler shape
already shown — but never explained — in
`kubernetes-objects-scheduling-and-networking.md`.

1. **Tolerance band suppresses noise** — metric samples at 68% and 72%
   (both within the 63–77% band around a 70% target) produce zero
   replica-count change. Real captured output: `actual-replicas=3` for
   both, no `CHANGED` marker.
2. **Scale-up applies immediately** — a real spike to 140% then 200% CPU
   scales the Deployment up in the very same sample, no stabilization
   delay: `t=620ms ... actual-replicas=6 <-- CHANGED`, then
   `t=922ms ... actual-replicas=12 <-- CHANGED` (clamped at `maxReplicas`).
3. **Scale-down is real, measurably delayed** — load drops to a steady
   20% CPU. The per-sample formula recommends scaling down starting at
   `t=1227ms`, but the controller doesn't actually shrink the fleet until
   `t=3052ms` — a real, measured ~1.8s stabilization delay (compressed
   from Kubernetes' real 300s default) — because the controller applies
   the *highest* recommendation seen across the whole stabilization
   window, not the latest one, exactly stopping a single low sample from
   immediately shrinking the fleet.

## Honest limitations

- The scale-down stabilization window is compressed to 2000ms so this
  demo runs in a few real seconds; the real Kubernetes default is 300
  seconds. The algorithm and the asymmetry it produces are the same real
  mechanism, only the window size is scaled down.
- This is a faithful model of the HPA *decision* algorithm (the formula,
  the tolerance band, the scale-up/scale-down asymmetry) — not a
  reimplementation of the full controller, which also averages a metric
  across multiple Pods, handles multiple simultaneous metrics, and
  accounts for Pods that aren't yet ready.
- The metric values are hand-fed to the demo, not pulled from a real
  `metrics-server` — the mechanism being proven is the controller's own
  decision logic once it has a metric value, which is the part that's
  actually under-documented, not the metrics pipeline itself.
