# Branching, Versioning, and Release — Real Git Transcripts

Backs [`syllabus/18-engineering-practices/branching-strategy-versioning-and-release-management.md`](../../../syllabus/18-engineering-practices/branching-strategy-versioning-and-release-management.md) (T-2439).

Real `git` (2.55.0), real repositories created in a temp directory and torn down afterwards. Commit hashes differ per run; the *structure* is the point.

## Run it

```bash
./run-demo.sh
```

Real output captured in [`output-transcript.txt`](output-transcript.txt).

## What it shows

**A — Trunk-based.** Two features, each on a short-lived branch, each merged into `main` and deleted, each release tagged. The final graph has one line and **one** surviving branch. There is exactly one place to ask "what is in production": the tag on `main`.

**B — Release branch, and what a hotfix actually costs.** A bug is found in `1.0` after `main` has moved on. The fix lands on `release/1.0` and is tagged `v1.0.1` — and `main` is still broken. Back-porting it with `git cherry-pick` **genuinely conflicted** in this run:

```text
Auto-merging app.txt
CONFLICT (content): Merge conflict in app.txt
error: could not apply 98836de... fix: clamp negative totals
```

That conflict is the real finding, and the demo resolves it the way a human would rather than pretending it did not happen. Afterwards the graph shows the identical change living on both lines under **two different commit hashes**, because a cherry-pick copies a change onto a new commit. That is precisely why a forgotten back-merge silently reintroduces a bug that was already fixed — the two lines have no shared ancestry for that change, so nothing warns you.

**C — Semantic versioning as a promise**, not a measure of effort: MAJOR means a caller must change, MINOR means new capability with existing callers unaffected, PATCH means a fix with no interface change. A one-line change that alters a response field is MAJOR; a thousand-line internal refactor with no API change is PATCH.
