---
title: "Cheat Sheet: Branching Strategy, Versioning, and Release Management"
slug: branching-strategy-versioning-and-release-management
document_type: cheat-sheet
domain: 18-engineering-practices
topic_id: T-2439
canonical: ../syllabus/18-engineering-practices/branching-strategy-versioning-and-release-management.md
last_updated: 2026-09-28
---

# Branching Strategy, Versioning, and Release Management

**Canonical chapter:** [`syllabus/18-engineering-practices/branching-strategy-versioning-and-release-management.md`](../syllabus/18-engineering-practices/branching-strategy-versioning-and-release-management.md)

## The Deciding Question

**Do we support more than one version in production at once?**
No → trunk-based. Yes → release branches, and accept the hotfix-twice cost.

## Models

| Model | Gains | Costs |
|---|---|---|
| Trunk-based | Fast integration, small merges, one place to look | Needs feature flags + strong CI |
| Release branches | Multiple live versions; stabilisation window | Every hotfix applied twice, with real conflict risk |
| GitFlow | Explicit ceremony, familiar | Heavy for continuous delivery |

## The Hotfix Cost, Measured

A real back-port in the demo **conflicted**:

```text
CONFLICT (content): Merge conflict in app.txt
error: could not apply 98836de... fix: clamp negative totals
```

After resolving, the same fix exists on both lines under **two different hashes** — `cherry-pick` applies a diff onto a new parent, so git has no record they are related. Nothing warns you if the back-port is forgotten. That is how a bug fixed in 1.0.1 reappears in 1.1.0.

**Prefer `git merge release/x` over `cherry-pick`** where history allows: ancestry then records the fix.

## Semantic Versioning = A Promise

| Bump | Means | Example |
|---|---|---|
| MAJOR | A caller must change | 1.2.3 → 2.0.0 |
| MINOR | New capability, callers unaffected | 1.2.3 → 1.3.0 |
| PATCH | Fix only, no interface change | 1.2.3 → 1.2.4 |

One line altering a response field = **MAJOR**. A thousand-line internal refactor = **PATCH**. The number describes the contract, not the diff.

Decide it with a testable question — *will any caller have to change?* — and automate the answer with an API-compatibility checker.

## Tags, Not Branches

A branch pointer moves hourly. A tag is immutable. "Which commit is in production?" should be answerable by a tag.

## Common Pitfalls

- GitFlow by default for a team with one live version.
- Long-lived feature branches — merge cost grows with divergence.
- Trunk-based without feature flags → tiny changes or half-shipped features.
- Bumping MAJOR for effort, PATCH for a breaking change.
- Cherry-pick as the standard back-port.

## Interview Answer Skeleton

**30-sec:** Trunk-based with short-lived branches and tags per release, paired with feature flags, unless we genuinely support several live versions — in which case release branches, and every hotfix costs two applications plus a conflict risk, with no tool warning if one is missed.

## Related

- [Git Internals and Collaboration Workflows](../syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md)
- [CI/CD Pipeline Design and Deployment Strategies](../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md)
- [API Versioning Strategies](../syllabus/07-api-design/api-versioning-strategies.md)
