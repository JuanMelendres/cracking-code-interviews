---
title: "T-1503 · Story Scope and Influence Reframing"
week: 5
last_reviewed: 2026-07-29
---

# T-1503 · Story Scope and Influence Reframing

**Rewrite Stories 1, 4, 7, and 8 (from Weeks 1, 2, 4) for scope and influence.** Same events — the facts don't change — but reframed to foreground who else was affected, who you had to convince, and what changed beyond your own team. This is scheduled now, not earlier, specifically because it's a rewrite of existing material, not new story construction.

## Table of Contents

1. [Why scope reframing, and why now](#1-why-scope-reframing-and-why-now)
2. [The reframing lens](#2-the-reframing-lens)
3. [Worked example](#3-worked-example)
4. [Your four reframes](#4-your-four-reframes)
5. [Exit check](#5-exit-check)

---

## 1. Why scope reframing, and why now

The same event, told at Senior scope, typically ends at "the decision was adopted for our service." Told at Staff scope, it continues: who else was affected by the decision, who had to be convinced (and what was their strongest counter-argument), and what changed beyond the immediate team. This is not embellishment — it's surfacing detail that was true of the original event but wasn't part of the Senior-scope telling. Doing this rewrite now, after 8 stories already exist (Weeks 1, 2, 4), means there's real material to mine rather than constructing scope artificially.

## 2. The reframing lens

For each story, answer:

| Question | What it surfaces |
|---|---|
| Who else was affected by this, beyond your immediate team? | Organizational scope |
| Who did you have to convince, and what was their strongest argument against you? | Influence, and intellectual honesty about the opposition |
| What changed as a result, beyond the original problem being solved? | Downstream impact |
| Would you make the same call again, knowing what you know now? | Judgment, not just execution |

## 3. Worked example

**Story 1 (architecture decision), Senior-scope version (from Week 1):** *"We chose hexagonal architecture for the order service because we needed to swap persistence technology later without a rewrite. It cost us mapping code but paid off when we actually did the swap."*

**Same story, Staff-scope reframe:** *"Two other teams consumed the order service's internal domain model directly at the time — a dependency that would have made any refactor risky. Convincing the platform lead to invest in the port/adapter boundary meant first convincing those two teams to go through the new port interface instead of the internals directly, which they resisted because it meant short-term rework on their side for a benefit they wouldn't see for months. The strongest argument against me was fair: 'we're slowing down two teams today for a benefit that's speculative.' The boundary held anyway; six months later, when the persistence swap happened, the two consuming teams didn't need to change anything, and one of them independently adopted the same port/adapter pattern for their own service afterward — an org-wide practice shift that outlasted the original technical reason for asking."*

**What changed in the reframe:** the same facts, but now visible: two other teams affected, a real objection represented fairly, and a downstream consequence (a practice adopted elsewhere) beyond the original service.

## 4. Your four reframes

```markdown
### Story 1 reframed (architecture decision)
Who else was affected:
Who you had to convince, their strongest argument:
What changed beyond the original problem:
Would you do it again:

### Story 4 reframed (technical debt / a refactor you argued for)
Who else was affected:
Who you had to convince, their strongest argument:
What changed beyond the original problem:
Would you do it again:

### Story 7 reframed (cross-team influence)
Who else was affected:
Who you had to convince, their strongest argument:
What changed beyond the original problem:
Would you do it again:

### Story 8 reframed (migration you led)
Who else was affected:
Who you had to convince, their strongest argument:
What changed beyond the original problem:
Would you do it again:
```

## 5. Exit check

All four reframes must name a real, specific, non-strawman objection someone raised — if a story's reframe has no genuine opposition to represent, that's worth noting honestly (not every real decision was contested) rather than inventing one for the exercise's sake.
