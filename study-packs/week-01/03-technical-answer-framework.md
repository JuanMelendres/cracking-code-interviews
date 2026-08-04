# T-1601 · Technical Answer Framework

**IWI 7.30 · Runs every week (W1–W6) · This chapter builds the method; §2 applies it in full to T-901**

**Canonical chapter:** [The Technical Answer Framework — Nine Layers](../../interview-playbook/technical-answers/technical-answer-framework.md). This file is the Week 1 study-pack entry point — a short summary of each section plus a link to the full canonical treatment. Section numbers below are kept stable because `11-week-1-checklist.md` and `study-packs/week-02/06-answer-frameworks.md` cite §2 and §3 directly.

---

## 1. Why this exists

Named interview feedback included "explain with greater depth" and "explain why you chose it" — a structure problem, not a knowledge problem: the audit's own finding was a mean answer length of ~110 characters, a single sentence, with no next layer to go to. Full reasoning and the fix: canonical chapter [§ Why This Exists](../../interview-playbook/technical-answers/technical-answer-framework.md#why-this-exists).

## 2. The nine-layer stack

Every topic marked **Deep** in the roadmap gets all nine layers — Opening, Senior answer, Deep dive, Whiteboard, Production example, Trade-offs, Traps, Follow-up chain, Staff extension — built and rehearsed before the interview, not improvised during it. Full table with lengths, purposes, and common failures per layer: canonical chapter [§ The Nine-Layer Stack](../../interview-playbook/technical-answers/technical-answer-framework.md#the-nine-layer-stack).

## 3. Worked in full — T-901 Hexagonal Architecture

The full nine-layer worked answer for T-901, built from `01-clean-hexagonal-architecture.md`, is elevated to the canonical chapter's own worked example rather than duplicated here: canonical chapter [§ Worked Example](../../interview-playbook/technical-answers/technical-answer-framework.md#worked-example).

## 4. Weekly build cadence

Six-day build schedule (Mon: L1–L2 written out, 45m · Tue: L5–L6 production example and trade-offs, 45m · Wed: L3 deep dive rehearsed aloud, 45m · Thu: L7–L8 traps and follow-up chain, 45m · Fri: record L1/L2/L6 for one topic and watch it back, 30m · Sat: L4 whiteboard + L9 Staff extension, 30m). The Friday recording step is not optional — full rationale: canonical chapter [§ Practice Cadence](../../interview-playbook/technical-answers/technical-answer-framework.md#practice-cadence).

## 5. Applying this to T-609 (this week's second Deep topic)

Repeat the same nine-layer construction against `02-database-index-fundamentals.md`. As a starting scaffold:

- **L1:** the §1 B+Tree definition, 30 seconds, no more.
- **L2:** the §3 before/after `EXPLAIN` numbers (5.754ms → 0.111ms) — a real measured example beats a hypothetical one every time.
- **L6 (the feedback-targeted layer):** §8's trade-off table, plus the §7 engine-specific correction (PostgreSQL vs InnoDB clustering) — naming which engine you mean, unprompted, is exactly the kind of depth signal layer 6 exists to produce.
- **L7:** the §5 note that a covering index makes an index-only scan *possible*, not *automatic* — a real trap, not a hypothetical one, since it required `SET enable_bitmapscan = off` to actually observe in this chapter's own lab.

## 6. Exercise

Before Friday's recording, write out L1 and L2 for T-609 from memory, without re-reading `02-database-index-fundamentals.md`. Then check against it. Any factual gap found this way is worth more than reading the chapter twice — it locates exactly what didn't stick.
