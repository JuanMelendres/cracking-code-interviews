---
title: "Flashcards: Technical Debt: Prioritization and Advocacy"
slug: technical-debt-prioritization-and-advocacy
document_type: flashcard-deck
domain: 19-leadership-staff
topic_id: T-1904
canonical: ../syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md
last_updated: 2026-09-07
---

# Flashcards: Technical Debt: Prioritization and Advocacy

**Canonical chapter:** [`syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md`](../syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md)

## Card: Technical debt is not "code I don't like"

**Prompt:**
What is Ward Cunningham's original technical debt metaphor, and what's the Foundation-level mistake in applying the term?

**Answer:**
Debt is the accepted shortcut of shipping a simpler, faster solution now with the understood cost of extra work required later — a deliberate, reasonable tool, like financial debt, or an unmanaged liability. The mistake is treating "technical debt" as a synonym for "code I don't like" — not every disliked pattern is debt, and conflating the two weakens the term's usefulness as a prioritization tool.

**Why it matters:**
Diluting the term makes genuinely costly debt harder to distinguish from stylistic preference when competing for prioritization.

**Common trap:**
Labeling any disliked code pattern as "debt" regardless of whether it was a conscious, understood trade-off.

**Related:**
[syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md](../syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md)

## Card: Fowler's technical debt quadrant

**Prompt:**
What are the two axes of Martin Fowler's technical debt quadrant, and which quadrant is most dangerous?

**Answer:**
Deliberate vs. inadvertent (was the shortcut a conscious trade-off or an accident of not knowing better) and reckless vs. prudent (was it made with clear understanding of its cost, or carelessly). Reckless, deliberate debt — "we don't have time to design this properly, let's just ship it" with no plan to revisit — is most dangerous, combining full awareness of risk with no accountability plan for repaying it.

**Why it matters:**
It's the prioritization tool for deciding which debt needs urgent attention (reckless-deliberate) versus which can reasonably wait (prudent trade-offs).

**Common trap:**
Treating all debt as equally urgent instead of using the quadrant to distinguish accountability-urgent debt from reasonable, lower-urgency trade-offs.

**Related:**
[syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md](../syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md)

## Card: Quantify debt in the stakeholder's own currency

**Prompt:**
Why does "this module's coupling makes any change take roughly 40% longer to ship" work better as a pitch than "this module has bad separation of concerns"?

**Answer:**
The first is a delivery-speed claim a product stakeholder can weigh against a feature's own delivery-speed cost in the same conversation; the second is a code-quality claim that isn't comparable to anything a non-engineering stakeholder is already prioritizing against.

**Why it matters:**
Presenting debt in code-quality terms rather than the stakeholder's own prioritization currency (delivery risk, delivery speed, incident frequency, direct cost) is the single most common reason a technically valid debt argument fails to get funded.

**Common trap:**
Pitching debt paydown in engineering-quality language that a business stakeholder has no comparable metric to weigh it against.

**Related:**
[syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md](../syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md)

## Card: Scoped proposals convert better than broad appeals

**Prompt:**
Why does "refactor OrderProcessor coupling, 2 engineer-weeks, ~40% faster checkout delivery" get funded more reliably than "we should invest more in code quality this quarter"?

**Answer:**
The scoped proposal has a bounded cost and a stated, falsifiable expected return — comparable to a feature proposal's own cost/benefit fields. The broad appeal has no comparable scope or expected return a stakeholder can weigh against a specific feature.

**Why it matters:**
It's the practical conversion mechanism that turns a general awareness of debt into fundable, prioritizable work.

**Common trap:**
Proposing broad, unscoped "quality investment" work instead of a specific, bounded item with a stated cost and effort.

**Related:**
[syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md](../syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md)

## Card: Why debt structurally loses prioritization fights

**Prompt:**
What is the actual mechanism behind debt paydown chronically losing to feature work in planning, according to this chapter?

**Answer:**
An asymmetric argument, not a lack of technical merit: a feature has a visible, near-term stakeholder actively advocating for it in every planning conversation, while debt paydown's benefit is diffuse, delayed, and has no natural advocate unless an engineer deliberately becomes one. The fix is giving debt the same concrete, near-term, comparable framing that features already have by default.

**Why it matters:**
It reframes debt advocacy from "make a better technical argument" to "give the argument the same structural framing feature work already gets."

**Common trap:**
Assuming debt keeps losing prioritization fights because the technical case wasn't strong enough, rather than because it lacked comparable, near-term framing.

**Related:**
[syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md](../syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md)

## Card: Standing capacity allocation vs. individually-pitched items

**Prompt:**
What problem does a standing capacity allocation (e.g., a fixed 15% of every sprint) solve that individually-pitched paydown proposals don't?

**Answer:**
Individually-pitched debt items compete for prioritization on their own merits every single time; a standing allocation removes that recurring competition for a bounded slice of capacity — at the cost of the team needing its own internal process to decide what that slice is spent on.

**Why it matters:**
At Staff scope, negotiating a standing allocation as a structural, ongoing commitment avoids repeatedly re-litigating debt priority item by item.

**Common trap:**
Treating a standing allocation as a free win with no trade-off — it still requires an accountable, visible internal ranking process, not an unaccountable block of time.

**Related:**
[syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md](../syllabus/19-leadership-staff/technical-debt-prioritization-and-advocacy.md)
