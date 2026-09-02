---
title: "Cheat Sheet: Git Internals and Collaboration Workflows"
slug: git-internals-and-collaboration-workflows
document_type: cheat-sheet
domain: cloud
topic_id: "N/A (no blueprint topic ID — see chapter's Topic register note)"
canonical: ../handbook/cloud/git-internals-and-collaboration-workflows.md
last_updated: 2026-09-02
---

# Git Internals and Collaboration Workflows

**Canonical chapter:** [`handbook/cloud/git-internals-and-collaboration-workflows.md`](../handbook/cloud/git-internals-and-collaboration-workflows.md)

## Core Mental Model

Git is a content-addressable object store (blobs, trees, commits, all named by the SHA of their own content) with branches as movable pointers into a directed acyclic graph of commits — nearly every git operation is either "create new objects" or "move a pointer," and almost nothing is ever destructively deleted until an explicit garbage collection. Once that clicks, `merge` vs `rebase` stops being two commands to memorize: merge creates one new commit with two parents and moves one pointer; rebase creates a run of brand-new commits (same content, new parents, therefore new hashes) and moves one pointer past them. Recovery tools like `reflog` follow directly: if operations mostly just move pointers, "undo" is often just "move the pointer back."

## Essential Definitions

- **The object model** — four types, all SHA-addressed by content: blob (raw file content, no filename), tree (a directory listing mapping names to blob/tree hashes), commit (a pointer to one tree plus parents, identity, message), tag (a named pointer to any object).
- **Branch** — a 41-byte text file holding one commit hash; `git commit` creates a new commit object and moves the branch pointer; `git reset` moves the pointer directly, with no new commit.
- **Merge** — produces one new commit with two parents, preserving both branches' original commits unchanged.
- **Rebase** — replays commits as brand-new objects with new parents and therefore new hashes, producing a linear history; unsafe on shared branches because it orphans anyone else's copy of the old commits.
- **Reflog** — a per-ref, local-only log of everywhere `HEAD`/a branch has pointed, stored under `.git/logs/`; recovery works because objects aren't deleted until GC, with a real expiry window (default ~30 days unreachable, ~90 days reachable).
- **Bisect** — automated binary search (`git bisect run <script>`) over commit history to find a regression in O(log n) test runs instead of O(n).

## Decision Table

| Question | If yes, lean toward |
|---|---|
| Has anyone else already pulled/based work on this branch? | Don't rebase — merge instead, or rebase only with explicit team agreement + `--force-with-lease` |
| Need exact chronology preserved for audit/compliance? | Merge — the merge commit is itself a permanent record |
| Goal is a clean, reviewable, linear PR before anyone else has seen the branch? | Interactive rebase (`git rebase -i`) to squash/reorder local-only commits |
| Trying to find which of many commits broke something? | `git bisect`, not manual `git log` scanning |
| Just made a destructive mistake (reset --hard, bad rebase)? | `git reflog` before anything else |

**Merge vs. rebase:**

| Concern | `git merge` | `git rebase` |
|---|---|---|
| History shape | Preserves exact chronology, forked-then-rejoined | Linear, rewritten |
| Commit identity | Original commits unchanged, safe on shared branches | New hashes for every replayed commit — unsafe on shared branches |
| Conflict resolution | Once, at the merge point | Once per replayed commit |

## Key Numbers (real, captured git 2.55.0 output against scratch repositories)

- Content-addressing proof: two files with identical content (`file.txt`, `file-copy.txt`) produce the exact same blob hash `3b18e512dba79e4c8300dd08aeb37f8e728b8dad` for `"hello world\n"`, regardless of filename.
- Merge vs. rebase on identical divergent history: merge produced 5 total commits (2-parent merge commit, C3 and C4 both preserved). Rebase produced 4 total commits — C3 replayed as a new object with hash `585ef61` versus the original `656a18e`.
- Reflog recovery: after `git reset --hard` to an old commit, `git cat-file -t <the "lost" commit hash>` still returned `commit` before any recovery step ran — the object was never removed.
- Bisect: a deliberately-planted regression at commit 3 of 5 was found in exactly 2 test executions via `git bisect run`.

## Common Pitfalls

- Treating `git rebase` as strictly "better" than merge because the log looks cleaner — that cleanliness comes at the cost of rewriting commit identity, unsafe the moment a branch is shared.
- Force-pushing with plain `--force` instead of `--force-with-lease`, silently discarding a teammate's concurrent push instead of failing safely.
- Panicking and trying to manually reconstruct lost work after a bad `reset --hard` instead of checking `git reflog` first.
- Assuming history rewrites make a leaked secret actually disappear — old commits remain fully recoverable (via reflog, other clones, forks) until a genuine rewrite is run *and* propagated everywhere; the only reliable fix is rotating the credential.

## Interview Answer Skeleton

**30-sec:** Git stores everything as content-addressed objects and branches are just movable pointers into that graph — which is why most operations are cheap and reversible. Merge creates a two-parent commit preserving both histories; rebase replays commits onto a new base, producing new hashes and a linear history but making it unsafe on shared branches. Reflog recovers from almost any local mistake because objects aren't actually deleted until garbage collection; bisect finds a regression's exact commit via binary search.

**2-min:** Add the real content-addressing proof (identical blob hash regardless of filename) and the real merge-vs-rebase result (5 commits with both C3/C4 preserved vs. 4 commits with C3 replayed under a new hash). Close with a force-push race prevented by `--force-with-lease` plus branch protection.

**Whiteboard:** Draw `C1 → C2`, fork into two branches each touching the same line ("guaranteed conflict, by construction"). Draw two endings side by side: a diamond merge commit with two arrows converging ("2 parents, C3 and C4 both survive unchanged") versus a straight continued line with `C3` relabeled `C3'` ("new hash — this is a DIFFERENT commit object").

**Staff-level framing:** Frame branching strategy as an organizational risk/velocity trade-off, not a technical preference — trunk-based development with short-lived branches suits high-velocity orgs, GitFlow-style long-lived branches suit infrequent, heavily-gated releases. Address the leaked-secret scenario at the process level: rotating the credential is necessary but insufficient without a process fix (secret-scanning pre-commit hooks or CI gates) preventing recurrence.

## Production Warning Signs

- A teammate's just-pushed commits silently vanish from a shared branch — a `git push --force` after a rebase discarded them from the remote (their local copy still has them). Prevention: `--force-with-lease` instead of plain `--force`, and branch protection rejecting force-pushes on protected branches.
- Giant, long-lived feature branches diverging from `main` for weeks — every day of divergence increases eventual conflict surface, defeating continuous integration.
- Committing directly to `main` with no PR/review gate — removes the one structural checkpoint most orgs rely on for quality and knowledge-sharing.
- A leaked credential "fixed" only by rewriting history without rotating it — the credential remains compromised regardless of what git history shows.

## Related

- `handbook/cloud/cicd-pipeline-design-and-deployment-strategies.md`
