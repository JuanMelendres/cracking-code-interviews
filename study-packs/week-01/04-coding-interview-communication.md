# T-1419 · Coding Interview Communication

**IWI — runs every coding session, all six weeks**

---

## 1. Why this exists

A correct solution delivered in silence scores lower than a correct solution narrated, because the interviewer is evaluating a process they can only see if you describe it: how you clarify ambiguity, how you choose an approach, how you reason about correctness before running anything. Silence forces the interviewer to guess whether you got lucky or reasoned your way there — and Staff-level loops explicitly weight the reasoning over the final answer.

## 2. The six-phase protocol

| Phase | What happens | What to say |
|---|---|---|
| **1. Clarify** | Confirm input shape, constraints, edge cases, and what "correct" means before writing anything | *"Can the array be empty? Are duplicates possible? Should I optimize for time or space if they trade off?"* |
| **2. State the invariant** | Name the approach and *why* it applies, before coding | *"This is a sliding-window problem because we need a contiguous run and the condition is monotonic as the window grows."* |
| **3. Complexity, upfront** | State expected time/space before writing code, not after | *"This should be O(n) time, O(k) space for the window."* |
| **4. Narrate while coding** | Say what each block does as you write it, not after | *"Advancing the right pointer, updating the count map."* |
| **5. Test before declaring done** | Walk at least one example by hand, including an edge case | *"Let me trace this on the empty-string case before I say I'm done."* |
| **6. State complexity, again, matching the code** | Confirm the actual complexity matches what was predicted in phase 3 | *"This came out O(n) time, O(min(n, charset size)) space, matching what I said."* |

## 3. Annotated failure transcripts

**Transcript A — skipping phase 1 (clarify)**
> *Candidate reads "Two Sum" and immediately starts coding a brute-force nested loop.*
> *Interviewer, 90 seconds in: "Can you assume the array is sorted?"*
> *Candidate: "...oh. No, I didn't check that."*

**What went wrong:** the constraint that unlocks the O(n) two-pointer solution (LC 167's sorted input) was available from the prompt and never asked about. Phase 1 exists specifically to surface this before committing to an approach.

**Transcript B — skipping phase 2 (state the invariant)**
> *Candidate writes a correct sliding-window solution in silence, then explains it after finishing.*
> *Interviewer: "Walk me through why you chose this approach before I saw the code."*
> *Candidate, retroactively: "Well, because... it's a contiguous substring problem, so..."*

**What went wrong:** the reasoning existed, but arrived after the artifact instead of before it — from the interviewer's side, this is indistinguishable from pattern-matching to a memorized template. Stating the invariant *before* coding is what separates recognition from memorization in the interviewer's read of the situation.

**Transcript C — skipping phase 5 (test before declaring done)**
> *Candidate: "That's my solution."*
> *Interviewer: "What does it return for an empty input?"*
> *Candidate traces it, finds an off-by-one, fixes it live, visibly rattled.*

**What went wrong:** not the bug itself — bugs happen — but that the edge case wasn't traced *before* declaring completion. A self-caught bug during phase 5 reads as rigor; the same bug caught by the interviewer after "I'm done" reads as carelessness, even though the code defect is identical either way.

## 4. Applying this to this week's problems

`07-java-coding-practice.md` contains the actual problems and their retrospectives. Narrate all six phases on every problem this week, out loud, even solo — the habit has to be automatic before Week 3's checkpoint, where it's scored alongside correctness.

## 5. Common mistakes

- Treating narration as a summary given *after* solving, rather than a running commentary *during* solving.
- Stating complexity once, at the start, and never checking the final code actually matches it (phase 6 exists precisely to catch this).
- Going silent while typing during phase 4 — the interviewer cannot follow a train of thought they can't hear.

## 6. Exercise

Solve one problem from `07-java-coding-practice.md` normally, then re-solve a different one this week narrating all six phases aloud, recorded. Compare: does phase 2 (stating the invariant before coding) change how confidently you choose the approach, even solo?
