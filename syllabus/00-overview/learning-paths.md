---
title: "Learning Paths"
document_type: syllabus-overview
status: Phase 1 outline extracted verbatim; Phase 6 assembly complete (2026-09-05) — all 6 real path documents now exist
last_updated: 2026-09-05
source: 00-project/syllabus-transformation-plan.md
---

> **Provenance note.** This file's content is extracted verbatim from `00-project/syllabus-transformation-plan.md` (approved 2026-09-03), not newly authored. The plan document remains the canonical source of record for any future dispute about intent; this file exists so the content lives at its designated `syllabus/` home per Phase 1 scaffolding.

# Learning Paths

## 6. Learning paths

Learning paths are **curated sequences of existing canonical topic references** — never copies of topic content. A path document is short: an ordered list of topic links, a stated time budget, and per-topic guidance on *which mastery level to stop at* for this path's purpose.

| Path | Audience / goal | Primary source material | Stops at level |
|---|---|---|---|
| **Junior → Mid** | New engineer building working competence | New CS-foundations + DSA content, `02-java` L1–L2, `06-databases` L1–L2, `08-testing` L1–L2 | L2 |
| **Mid → Senior** | Working engineer building depth and production judgment | `02-java`–`17-architecture` domains, L2–L3, with production-cookbook debugging exercises | L3 |
| **Senior interview refresh** | Experienced engineer with an upcoming interview, needs recall not new learning | Existing `flashcards/`, `cheat-sheets/`, D1–D13 topics at L3, `20-interview-preparation` | L3 (review only) |
| **Senior → Staff** | Senior engineer building systemic/organizational judgment | `10-distributed-systems`, `11-system-design`, `17-architecture`, `19-leadership-staff`, all domains' L4 sections | L4 |
| **Interview emergency sprint** | ≤8 weeks to a known interview loop | **This is `study-packs/` Plan A, referenced wholesale, unchanged** — the existing 6-week, IWI-ranked, feedback-driven sprint | L3, selectively L4 |
| **Backend Java specialization** | Deep-dive into the primary track specifically | `02-java`, `05-spring`, `06-databases`, `09-messaging-event-driven`, `16-performance-jvm`, all levels | L4 |

Two of these six paths (**Interview emergency sprint**, and much of **Senior interview refresh**) are not new — they are `study-packs/` and the existing `interview-playbook/`/`flashcards/`/`cheat-sheets/` layer, given a name and a place in the new taxonomy's navigation, with zero content migration required. This is deliberate: it demonstrates the "same canonical topics, different route through them" principle using material that already exists, rather than asking the reader to trust an untested new mechanism.

---

## Phase 6 update (2026-09-05) — all six paths assembled as real documents

The table above is the Phase 1 outline, kept verbatim per this file's own provenance note. Phase 6 (learning-path assembly, per §10 of the transformation plan) is now complete: each of the six paths named above is a real, short document — an ordered list of real topic links, a stated time budget, and per-topic stop-at guidance — living in [`syllabus/00-overview/learning-paths/`](learning-paths/):

| Path | Document |
|---|---|
| Junior → Mid | [`learning-paths/junior-to-mid.md`](learning-paths/junior-to-mid.md) |
| Mid → Senior | [`learning-paths/mid-to-senior.md`](learning-paths/mid-to-senior.md) |
| Senior interview refresh | [`learning-paths/senior-interview-refresh.md`](learning-paths/senior-interview-refresh.md) |
| Senior → Staff | [`learning-paths/senior-to-staff.md`](learning-paths/senior-to-staff.md) |
| Interview emergency sprint | [`learning-paths/interview-emergency-sprint.md`](learning-paths/interview-emergency-sprint.md) |
| Backend Java specialization | [`learning-paths/backend-java-specialization.md`](learning-paths/backend-java-specialization.md) |

Two path-construction choices worth stating explicitly, since they shape how these six documents differ from each other structurally, not just in content: **Junior → Mid** and **Senior → Staff** name individual topics (a genuine cross-domain curation — picking specific chapters out of larger domains), while **Mid → Senior** and **Backend Java specialization** sequence whole domains and point to each domain's own `INDEX.md` as the exhaustive topic list, rather than re-listing every topic inside them — re-listing would duplicate the domain index rather than add sequencing value on top of it, which is exactly the duplication this project's canonical-ownership rule exists to prevent. **Interview emergency sprint** adds no new content at all — it points at `study-packs/`, unchanged, exactly as this Phase 1 outline already specified.

All six documents' links were verified to resolve on disk (zero broken links) as part of this assembly pass.

---
