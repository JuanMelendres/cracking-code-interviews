# Java Regular Expressions — Real Demo

Backs [`syllabus/02-java/language-core/java-regular-expressions.md`](../../../../syllabus/02-java/language-core/java-regular-expressions.md) (T-2430).

Pure JDK, no dependencies.

## Run it

```bash
mkdir -p out
javac -d out src/RegexDemo.java
java -cp out RegexDemo
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it proves

- **Named groups** — `(?<year>\d{4})-(?<month>\d{2})-(?<day>\d{2})` extracting
  structured fields by name, not positional index.
- **Greedy vs. reluctant vs. possessive**, all three run against the
  identical input (`<a><b><c>`) — the greedy quantifier consumes the whole
  string then backtracks, the reluctant one stops at the first valid match,
  and the possessive one genuinely fails to match at all, because it never
  gives back characters it already consumed.
- **A real, measured 5.3x speedup** from precompiling a `Pattern` once
  versus calling `String.matches()` (which compiles the pattern fresh on
  every single call) across 200,000 iterations.
- **Real, measured catastrophic backtracking** — a pattern with 15 adjacent
  `a*` quantifiers before a required literal shows genuine exponential time
  growth (34 ms at 10 characters, 13.4 seconds at 19) against this exact
  JDK. A side-note in the transcript also records an honest negative result:
  the textbook nested-group ReDoS shapes (`(a+)+`, `(a|aa)+`) did **not**
  reproduce this blowup on this JDK even at 45 repetitions — a real,
  current finding worth knowing, not folklore.
