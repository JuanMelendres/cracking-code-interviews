---
title: "Cheat Sheet: Technical Debt: Prioritization and Advocacy"
slug: technical-debt-prioritization-and-advocacy
document_type: cheat-sheet
domain: 19-leadership-staff
topic_id: T-1904
canonical: ../syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md
last_updated: 2026-09-06
---

# Technical Debt: Prioritization and Advocacy

**Canonical chapter:** [`syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md`](../syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md)

## Core Mental Model

Debt paydown structurally loses prioritization fights against feature work not because it lacks technical merit, but because features have a visible, near-term advocate in every planning conversation while debt's benefit is diffuse and delayed unless an engineer deliberately gives it the same concrete, comparable framing — stated in the stakeholder's own currency (delivery speed, risk, incident frequency), never in code-quality terms alone.

## Essential Definitions

- **Technical debt** (Cunningham) — the accepted shortcut of shipping simpler now at the cost of extra work later; not a synonym for "code I don't like."
- **Fowler's quadrant** — deliberate vs. inadvertent × reckless vs. prudent. *Reckless, deliberate* debt (a known shortcut with no repayment plan) is the most dangerous quadrant, combining full awareness of risk with no accountability plan.
- **Debt inventory** — a written list, each item with a stated cost and fix effort, that can be ranked in the same backlog and against the same criteria as feature work.
- **Stakeholder-currency framing** — quantify debt as a delivery-speed, risk, or incident-frequency claim, not a code-quality claim, so a non-engineering stakeholder can weigh it against a feature.
- **Standing capacity allocation** — a fixed percentage of sprint capacity dedicated to paydown, removing recurring per-item prioritization competition, at the cost of needing its own internal allocation process.

## Decision Table — What to Pay Down First

| Debt characteristic | Priority |
|---|---|
| Reckless + deliberate (known risk, no repayment plan) | Highest — most dangerous quadrant, needs urgent accountability |
| Prudent trade-off (conscious, understood cost) | Lower urgency — can reasonably wait |
| High measured cost, low fix effort | Favor first among comparably-urgent items, all else equal |
| Cost real but hard to quantify (e.g., low-probability, high-impact security risk) | State the uncertainty explicitly (a range), don't inflate to a false-precision number |

## Common Pitfalls

- Presenting debt in code-quality terms rather than the stakeholder's own prioritization currency — the single most common reason a technically valid debt argument fails to get funded.
- Proposing broad, unscoped "let's invest in quality" work instead of a specific, bounded item with a stated cost and effort.
- Treating every disliked pattern as debt — dilutes the term and makes genuinely costly debt harder to distinguish.
- Letting debt go unmeasured, relying on it being "obviously bad" — invisible cost systematically loses to visible feature demand regardless of its actual size.

## Interview Answer Skeleton

**30-sec:** Debt paydown gets funded by reframing it in the stakeholder's own currency — a delivery-speed tax or incident-frequency number — in a narrowly scoped, bounded proposal, not by a general "we should invest in quality" appeal or a code-quality argument alone.

**2-min:** Add the mechanism: debt paydown competes against feature work asymmetrically because features have a visible near-term advocate and debt doesn't, unless someone deliberately becomes one. Concretely, "this module's coupling makes changes take ~40% longer" is comparable to a feature's own cost/benefit fields; "bad separation of concerns" is not. Debt that isn't measured tends to be underestimated in cost and overestimated in fix effort — deliberately measuring both corrects the bias in both directions.

**Whiteboard:** Draw a debt-inventory entry as four labeled lines — Item, Cost (measured), Fix effort, Expected return — and say: "every field here is comparable to a feature proposal's own cost/benefit fields, which is exactly why it's fundable in the same planning conversation."

**Staff-level framing:** At Staff scope, debt advocacy becomes a portfolio decision — negotiating a standing capacity allocation as a structural commitment rather than relitigating each item, and noticing when several unrelated-seeming debt items across teams share one root cause (an absent architectural fitness function) worth fixing structurally instead of symptom by symptom. A Staff engineer's advocacy also carries more weight, which comes with a responsibility to quantify debt honestly rather than overstate urgency — overstated claims erode the credibility needed for the next, genuinely urgent one.

## Warning Signs

- A debt paydown item sits on the backlog for several quarters, repeatedly deprioritized despite everyone agreeing it's a real problem — check first whether it has ever actually been quantified in stakeholder-comparable terms (a measured delivery-speed or incident cost) versus only described in code-quality terms, which structurally cannot win against a feature with a stated near-term stakeholder. A real, documented instance: a core class's coupling grew one or two collaborators at a time across a dozen individually-reasonable pull requests, invisible until its cumulative ~40% velocity cost surfaced independently — no debt-inventory entry existed for it until then.

## Related

- syllabus/17-architecture/technical-debt-and-evolutionary-architecture.md
- syllabus/19-leadership-staff/leading-migrations-and-large-technical-change.md
- syllabus/20-interview-preparation/behavioral/11-technical-debt-advocacy.md
