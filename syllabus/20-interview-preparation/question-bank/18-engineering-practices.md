---
title: "Interview Question Bank — 18-engineering-practices"
document_type: interview-question-bank
domain: 20-interview-preparation
status: in progress
version: 1.0
last_updated: 2026-09-13
related:
  - ../../18-engineering-practices/INDEX.md
  - 17-architecture.md
  - ../../../00-project/interview-question-bank-plan.md
---

# Interview Question Bank — Engineering Practices

Part of the multi-domain compendium. See [`06-databases.md`](06-databases.md) for the
tier-explanation format and `00-project/interview-question-bank-plan.md` for the full
22-domain plan and sourcing discipline.

**Honest count for this domain:** 6 chapters yielded 12 deep questions + 2 quick-fire
questions = **14 real questions**. No Junior Fundamentals chapter exists in this
domain. Five of six chapters use the older numbered `## 15. Interview Questions`
template with no Flashcards section at all; only `git-internals-and-collaboration-workflows.md`
has a `### Card:` Flashcards section.

---

## Architecture Decision Records and Technical Writing for Engineers

### Q1 — What are the required sections of an ADR, and why does "Consequences" need to include negative consequences specifically?

**Canonical treatment:** [§15, Q1](../../18-engineering-practices/architecture-decision-records-and-technical-writing.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Names the sections correctly but can't explain why any specific one matters, revealing template-level rather than practice-level familiarity.
- **Senior:** States the negative-consequences point unprompted as the single most reliable quality signal, and can describe a genuinely honest Consequences section versus a hollow one.
- **Staff:** Connects this to organizational ADR-corpus health — auditing a team's existing ADRs specifically for this pattern as a real, high-leverage quality-improvement activity.

### Q2 — Why should you supersede an old ADR with a new one rather than editing the original in place when a decision changes?

**Canonical treatment:** [§15, Q2](../../18-engineering-practices/architecture-decision-records-and-technical-writing.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Treats an ADR as a living document to be kept perpetually up to date — the common mistake this question targets, conflating it with ordinary reference documentation.
- **Senior:** Gives a concrete example of when the old reasoning remains useful even after being superseded.
- **Staff:** Connects this to the broader "ADR corpus as compounding institutional memory" argument — a superseded-but-preserved chain shows how a system's architecture evolved and why.

---

## Code Review: Standards and Practice

### Q1 — How do you decide what feedback in a code review is blocking versus optional?

**Canonical treatment:** [§15, Q1](../../18-engineering-practices/code-review-standards-and-practice.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Treats every comment as equally mandatory, forcing authors to either push back or accumulate unnecessary churn — the common mistake this question targets.
- **Senior:** Names a specific, consistent labeling convention and can explain why the distinction matters for author trust and review-cycle speed.
- **Staff:** Connects this to team-level review-culture design — establishing this convention as a team norm and monitoring whether real behavior reflects a healthy balance between rigor and speed.

### Q2 — Why does keeping code changes small improve review quality, beyond just being faster to read?

**Canonical treatment:** [§15, Q2](../../18-engineering-practices/code-review-standards-and-practice.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** States that smaller changes are easier to review, even without the specific cognitive-load mechanism.
- **Senior:** Articulates the non-linear degradation explicitly and can describe concrete splitting techniques (separating a refactor from the feature change it enables).
- **Staff:** Connects this to the compounding-latency argument as a second, related lever affecting overall team throughput.

---

## Git Internals and Collaboration Workflows

### Q1 — What's actually different about the resulting git history between `git merge` and `git rebase`, and why does that matter for a branch other people have already pulled?

**Canonical treatment:** [§ Interview Questions, Q1](../../18-engineering-practices/git-internals-and-collaboration-workflows.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Describes the difference purely in terms of "how the log looks" without mentioning new commit objects — the common mistake this question targets.
- **Senior:** States the new-hash consequence unprompted, not just the visual log-shape difference — rebase creates entirely new commit objects, diverging anyone's already-pulled history.
- **Staff:** Frames the choice as a policy decision (branch protection, `--force-with-lease` as a team convention) rather than a per-engineer judgment call.

### Q2 — You ran `git reset --hard` to the wrong commit and lost what looks like two commits of work. What do you actually do?

**Canonical treatment:** [§ Interview Questions, Q2](../../18-engineering-practices/git-internals-and-collaboration-workflows.md#interview-questions)

**What's expected:**
- **Junior/Mid:** Tries to manually recreate the lost commits, or believes the work is unrecoverable — the common mistake this question targets.
- **Senior:** Names `git reflog` immediately, without needing to be walked toward it — the commits were never deleted, only the branch pointer moved.
- **Staff:** Mentions the reflog's local-only, expiry-windowed nature, and what that implies for team process (don't rely on reflog as a substitute for pushing to a shared remote regularly).

---

## Refactoring Discipline

### Q1 — What's the precise definition of "refactoring," and why does that precision matter in practice?

**Canonical treatment:** [§15, Q1](../../18-engineering-practices/refactoring-discipline.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Uses "refactor" to describe any restructuring regardless of whether behavior changed — the common mistake this question targets, diluting the term's usefulness as a signal to reviewers.
- **Senior:** Names the review-scrutiny consequence explicitly and can describe a concrete, mechanical way to verify a change is a pure refactor.
- **Staff:** Connects this to establishing the distinction as an explicit team review norm — requiring "refactor" and "behavior change" to be separate commits/PRs.

### Q2 — Why should the test suite remain completely unmodified across a refactor, rather than being updated to match the new code structure?

**Canonical treatment:** [§15, Q2](../../18-engineering-practices/refactoring-discipline.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "the tests needed minor updates" as an acceptable, normal part of refactoring — the common mistake this question targets.
- **Senior:** Explains the logical argument precisely — tests check behavior, unchanged tests passing means unchanged behavior, verified rather than assumed.
- **Staff:** Connects this to a real, concrete parity-test example and generalizes it to a team review policy: any PR labeled "refactor" that includes test changes should be specifically questioned.

---

## SDLC and Agile Methodology Fundamentals

### Q1 — What are the phases of the SDLC, and how does Agile change how a team moves through them?

**Canonical treatment:** [§15, Q1](../../18-engineering-practices/sdlc-and-agile-methodology-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Names Agile/Scrum ceremonies (standup, retrospective) as if they were SDLC phases themselves — the common mistake this question targets.
- **Senior:** Can name a real context where Waterfall is still the defensible choice.
- **Staff:** Connects process-model choice to actual risk profile and organizational context, not treated as a default preference.

### Q2 — What's the difference between Agile and Scrum?

**Canonical treatment:** [§15, Q2](../../18-engineering-practices/sdlc-and-agile-methodology-fundamentals.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Treats "Agile," "Scrum," and "sprint" as fully interchangeable terms — the common mistake this question targets.
- **Senior:** Can name at least the Scrum artifacts and roles and briefly explain what each one is for.
- **Staff:** Can diagnose a team practicing "Scrum in name only" and identify the specific missing practice undermining it.

---

## Working with Legacy Code

### Q1 — You need to make a change to a method with no existing tests, in code you don't fully understand. What's your process?

**Canonical treatment:** [§15, Q1](../../18-engineering-practices/working-with-legacy-code.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Jumps straight to refactoring or changing the code before establishing any characterization tests — the common mistake this question targets.
- **Senior:** Explicitly separates the probing step from the asserting step, and can explain why assertions based on assumed rather than observed behavior defeat the technique's purpose.
- **Staff:** Connects this to a broader judgment call — recognizing when incremental characterization-and-change is right versus when risk has grown enough to warrant a larger migration strategy.

### Q2 — What's the difference between a characterization test and a normal unit test that verifies correct behavior?

**Canonical treatment:** [§15, Q2](../../18-engineering-practices/working-with-legacy-code.md#15-interview-questions)

**What's expected:**
- **Junior/Mid:** Treats a characterization test suite as equivalent to a specification-verifying test suite — the common mistake this question targets, conflating "pinned down" with "correct."
- **Senior:** Gives a concrete example (a discount-cliff finding) of a characterization test capturing genuinely surprising behavior without endorsing it as correct.
- **Staff:** Connects this to the deliberate-update discipline — when characterized behavior is intentionally changed later, the assertion should be updated with a clear note, treating the suite as a living record.

---

## Quick-fire questions (from this domain's Flashcards)

| # | Question | Canonical chapter |
|---|---|---|
| 1 | Does `git rebase` change the hash of the commits it replays? | [Git Internals and Collaboration Workflows](../../18-engineering-practices/git-internals-and-collaboration-workflows.md#flashcards) |
| 2 | Why does `git reflog` recover work after a `git reset --hard`? | [Git Internals and Collaboration Workflows](../../18-engineering-practices/git-internals-and-collaboration-workflows.md#flashcards) |

---

## Related

- [`17-architecture.md`](17-architecture.md)
- [`16-performance-jvm.md`](16-performance-jvm.md)
- [`15-cloud.md`](15-cloud.md)
- [`14-devops-containers.md`](14-devops-containers.md)
- [`13-observability.md`](13-observability.md)
- [`12-security.md`](12-security.md)
- [`11-system-design.md`](11-system-design.md)
- [`10-distributed-systems.md`](10-distributed-systems.md)
- [`09-messaging-event-driven.md`](09-messaging-event-driven.md)
- [`08-testing.md`](08-testing.md)
- [`07-api-design.md`](07-api-design.md)
- [`05-spring.md`](05-spring.md)
- [`04-software-design.md`](04-software-design.md)
- [`03-data-structures-algorithms.md`](03-data-structures-algorithms.md)
- [`06-databases.md`](06-databases.md)
- [`02-java-collections.md`](02-java-collections.md), [`02-java-concurrency.md`](02-java-concurrency.md), [`02-java-jvm-internals.md`](02-java-jvm-internals.md), [`02-java-language-core.md`](02-java-language-core.md)
- [`00-project/interview-question-bank-plan.md`](../../../00-project/interview-question-bank-plan.md)
