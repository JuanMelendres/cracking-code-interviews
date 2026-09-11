# Metric Cardinality and Alert Fatigue — Real Demo

Backs [`syllabus/13-observability/metric-cardinality-and-alert-fatigue.md`](../../../../syllabus/13-observability/metric-cardinality-and-alert-fatigue.md) (T-2409).

Two real, deterministic demos: a real Micrometer `SimpleMeterRegistry`
(pure JDK, no metrics server needed) proving cardinality explosion
directly, and a real, seeded simulation proving the standard
multi-window burn-rate alerting technique's real noise reduction.

## Run it

```bash
./fetch-deps.sh
./run.sh
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **A high-cardinality label creates one real, distinct time series per
  unique value — not a bounded number.** Sending the identical 20,000
  requests through two real Micrometer registries — one tagged by a
  bounded route + status code, one tagged by route + a raw, per-request
  UUID — produces 3 real distinct `Meter`s in the first and 20,000 in
  the second: a real, measured 6,667x more time series for identical
  traffic, with a real, measured ~43x heap delta to match. This is the
  literal, mechanical shape of a metrics-cost incident, not a
  metaphor.
- **Multi-window burn-rate alerting really does reduce alert volume
  while still catching real incidents.** A real, seeded 7-day synthetic
  error-rate series (realistic background noise, occasional
  single-minute blips, and two genuine, sustained incidents) is
  evaluated under two real rules: a naive "fires above 1% in any single
  minute" threshold, and Google SRE Workbook's real, published
  multi-window burn-rate rule (burn rate ≥ 14.4, sustained across both
  a 5-minute and a 1-hour window simultaneously, for page-severity
  alerts on a 99.9% SLO). The naive rule fires 28 times; the burn-rate
  rule fires 2 times — a real 14x reduction — while both rules still
  catch both real injected incidents, verified directly, not assumed.
