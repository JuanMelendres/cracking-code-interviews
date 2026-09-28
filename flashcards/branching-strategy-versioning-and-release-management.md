---
title: "Flashcards: Branching Strategy, Versioning, and Release Management"
slug: branching-strategy-versioning-and-release-management
document_type: flashcard-deck
domain: 18-engineering-practices
topic_id: T-2439
canonical: ../syllabus/18-engineering-practices/branching-strategy-versioning-and-release-management.md
last_updated: 2026-09-28
---

# Flashcards: Branching Strategy, Versioning, and Release Management

**Canonical chapter:** [`syllabus/18-engineering-practices/branching-strategy-versioning-and-release-management.md`](../syllabus/18-engineering-practices/branching-strategy-versioning-and-release-management.md)

## Card: The question that decides the branching model

**Prompt:**
Trunk-based or release branches — what decides it?

**Answer:**
Whether you support more than one version in production simultaneously. A SaaS product with one live version has no need for release branches, and adopting them anyway imports the hotfix-twice cost for nothing. A library, a mobile app with users on old builds, or an on-premise product genuinely does, and the cost is the price of that commitment.

**Why it matters:**
It turns a taste argument into a question with a factual answer.

**Common trap:**
Choosing GitFlow by default because it is familiar, while deploying several times a day.

**Related:**
[Branching Strategy, Versioning, and Release Management](../syllabus/18-engineering-practices/branching-strategy-versioning-and-release-management.md)

## Card: Why a forgotten back-port is silent

**Prompt:**
A hotfix is cherry-picked from a release branch to `main`. Why can git not warn you if someone skips it?

**Answer:**
Because `cherry-pick` applies the same *diff* onto a different parent, producing a genuinely different commit object — verified in the demo, the identical fix exists on two lines under two different hashes. Git has no record that the two commits are related, so there is nothing for it to notice. That is exactly how a bug fixed in 1.0.1 reappears in 1.1.0.

**Why it matters:**
It is the concrete cost of release branches, beyond "extra branches to manage."

**Common trap:**
Assuming back-porting is a formality — the captured run produced a real `CONFLICT (content)` while doing it under incident pressure.

**Related:**
[Git Internals and Collaboration Workflows](../syllabus/18-engineering-practices/git-internals-and-collaboration-workflows.md)

## Card: Merge or cherry-pick for a back-port?

**Prompt:**
When should you merge the release branch instead of cherry-picking the fix?

**Answer:**
Whenever history allows. A merge creates a commit whose ancestry includes the hotfix, so git knows the fix is present and a later merge will not re-apply it. A cherry-pick leaves the ancestry disconnected, so a subsequent merge of the release branch can re-apply the change and conflict with the copy already there.

**Why it matters:**
It is the difference between "we fixed it in 1.0.1" being verifiable and being a claim.

**Common trap:**
Cherry-picking by habit because it feels more surgical.

**Related:**
[Branching Strategy, Versioning, and Release Management](../syllabus/18-engineering-practices/branching-strategy-versioning-and-release-management.md)

## Card: What a version number promises

**Prompt:**
A one-line change altered a response field. What version bump?

**Answer:**
MAJOR. Per SemVer, MAJOR means a caller must change to keep working, MINOR means new capability with existing callers unaffected, PATCH means a fix with no interface change. The number describes the **contract**, not the diff — so a thousand-line internal refactor with no interface change is PATCH.

**Why it matters:**
The reliable decision procedure is a testable question — *will any caller have to change?* — ideally answered by an API-compatibility checker in CI rather than by a vote about significance.

**Common trap:**
Bumping for effort, and staying on `0.x` once real consumers depend on you, which puts you outside the guarantee by definition.

**Related:**
[API Versioning Strategies](../syllabus/07-api-design/api-versioning-strategies.md)

## Card: Why trunk-based needs feature flags

**Prompt:**
What makes "merge to main daily" compatible with "do not release it yet"?

**Answer:**
Feature flags. Without a way to hide unfinished work, trunk-based development forces either artificially small changes or half-shipped features. The flag moves the release decision out of version control and into runtime configuration — which is also what makes progressive delivery (canary, percentage rollout) possible.

**Why it matters:**
It is the part people omit when they describe trunk-based development, and its absence is why their attempt at it failed.

**Common trap:**
Flags that never get removed, accumulating into permanent conditional complexity.

**Related:**
[CI/CD Pipeline Design and Deployment Strategies](../syllabus/14-devops-containers/cicd-pipeline-design-and-deployment-strategies.md)
