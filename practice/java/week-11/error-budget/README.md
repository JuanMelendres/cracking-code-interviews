# Week 11 Java — Error Budget Simulation — runnable verification

One real demo. No external dependencies.

## Setup and run

```bash
cd practice/java/week-11/error-budget
mkdir -p out
javac -d out src/ErrorBudgetDemo.java
java -cp out ErrorBudgetDemo
```

**Real observed output (last run, abbreviated):**

```
SLO: 99.900% success rate over 30 days (60,000,000 total requests)
Error budget: 60,000 allowed failures over the period (0.1000% of traffic)

day 16:      446 failures, budget remaining after today:     53,143
day 17:    8,791 failures, budget remaining after today:     44,352  <-- incident day
day 18:      402 failures, budget remaining after today:     43,950
...
Actual success rate over the 30 days: 99.96492%  (SLO target: 99.900%)
Total failures: 21,050 of 60,000 allowed (35.1% of budget consumed)
RESULT: SLO met -- budget remaining, but see day 17's single-day burn rate.
```

**What this proves:** a simulated day-17 incident (8,791 failures vs. a ~400-450 background rate on every other day) consumed roughly 8,350 more failures than a typical day — by itself, close to 14% of the ENTIRE 30-day error budget, in a single 40-minute window, while the month as a whole still met its SLO. Computed directly, not asserted.
