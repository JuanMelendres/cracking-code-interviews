---
title: "Cheat Sheet: Java Regular Expressions"
slug: java-regular-expressions
document_type: cheat-sheet
domain: 02-java/language-core
topic_id: T-2430
canonical: ../syllabus/02-java/language-core/java-regular-expressions.md
last_updated: 2026-09-26
---

# Java Regular Expressions

**Canonical chapter:** [`syllabus/02-java/language-core/java-regular-expressions.md`](../syllabus/02-java/language-core/java-regular-expressions.md)

## Core Mental Model

If a regex has multiple quantifiers that could consume the same characters in different ways, ask how many ways a backtracking engine could try before giving up — that count, not the regex's readability, determines worst-case cost.

## Essential Definitions

- **`Pattern`** — a compiled, immutable, thread-safe regex; compilation is the expensive step, cache and reuse it.
- **`Matcher`** — stateful, per-input, NOT thread-safe; produced by `pattern.matcher(input)`.
- **Greedy (`.+`)** — consumes maximally, backtracks if needed.
- **Reluctant (`.+?`)** — consumes minimally, expands only on failure.
- **Possessive (`.++`)** — consumes maximally, never backtracks — can genuinely fail where greedy succeeds.

## Decision Table

| Symptom | Likely cause | Fix |
|---|---|---|
| Regex-heavy hot path is slow | `String.matches()`/`replaceAll()`/`split()` recompiling every call | Cache a `Pattern` as a `static final` field |
| A pattern that "should" match fails | A possessive quantifier consumed characters the rest needed back | Use the greedy version if backtracking is genuinely needed |
| A request hangs on certain input | Ambiguous, overlapping quantifiers (nested or adjacent) + adversarial input | Cap input length; remove the ambiguity; add a worst-case-time test |
| Unsure if a regex is ReDoS-safe | Assumed safety from a specific textbook example | Measure the actual pattern's worst-case time directly |

## Common Pitfalls

- Calling `String.matches()` repeatedly with the same regex — a real, measured 5.3x avoidable cost in this chapter's own demo.
- Treating possessive quantifiers as "greedy but faster" with identical results — they can genuinely change what matches.
- Trusting a memorized "evil regex" example as universal — this chapter measured `(a+)+`/`(a|aa)+` NOT blowing up on the current JDK, while an adjacent-quantifier shape DID (34ms to 13.4s, n=10 to 19).

## Interview Answer Skeleton

**30-sec:** `Pattern` is compiled once and should be cached; `Matcher` is per-input and stateful. Greedy/reluctant/possessive genuinely differ in what matches, not just speed. Catastrophic backtracking is real and measurable for ambiguous quantifier shapes — verified with real exponential time growth.

**2-min:** Add: measured 5.3x precompilation speedup; a real possessive-vs-greedy match-result divergence; real exponential backtracking blowup from adjacent quantifiers, plus an honest negative result that some textbook ReDoS examples don't reproduce on the current JDK.

**Staff-level framing:** any regex applied to untrusted input needs an explicit worst-case-time check before shipping, as a standing security-review item — not an assumption based on folklore about which patterns are dangerous.

## Related

- syllabus/12-security/injection-input-validation-output-encoding.md
- syllabus/02-java/language-core/strings-interning-compact-strings-and-builders.md
