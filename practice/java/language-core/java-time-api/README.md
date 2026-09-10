# java.time API — Real Demo

Backs [`syllabus/02-java/language-core/java-time-api.md`](../../../../syllabus/02-java/language-core/java-time-api.md) (T-116).

Pure JDK, no dependencies.

## Run it

```bash
mkdir -p out
javac -d out src/JavaTimeApiDemo.java
java -cp out JavaTimeApiDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **Real immutability** — `LocalDate.plusMonths()` returns a new object,
  leaving the original untouched; contrasted directly against a real
  `java.util.Calendar.add()` call mutating the same object in place.
- **Four types, one moment** — `LocalDate`/`LocalDateTime`/`ZonedDateTime`/`Instant`
  derived from the identical `ZonedDateTime`, printed side by side.
- **Period vs. Duration, including a real DST divergence** — starting from
  `2026-03-07T12:00` in `America/New_York` (the day before a real US
  spring-forward), `plus(Period.ofDays(1))` lands at `12:00` the next day
  while `plus(Duration.ofDays(1))` lands at `13:00` — the same starting
  point, the same nominal "1 day," genuinely different real results,
  because that specific calendar day only had 23 real hours.
- **A real, reproduced `SimpleDateFormat` thread-safety bug** — 20 threads x
  500 concurrent `parse()` calls against one shared `SimpleDateFormat`
  produced real corrupted/failed results (this run: 6969 out of 10,000 —
  the exact count is timing-dependent and will vary by run and machine,
  but corruption reliably occurs at this thread/iteration count on a
  multi-core machine). The identical workload against a shared
  `DateTimeFormatter` produced zero corrupted results, real evidence that
  `java.time`'s immutability structurally prevents this entire bug class.
