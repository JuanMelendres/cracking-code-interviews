---
title: "Flashcards: Explaining Technical Concepts Under Pressure"
slug: explaining-technical-concepts-under-pressure
document_type: flashcard-deck
domain: 20-interview-preparation/technical-answers
topic_id: T-1606
canonical: ../syllabus/20-interview-preparation/technical-answers/explaining-technical-concepts-under-pressure.md
last_updated: 2026-09-30
---

# Flashcards: Explaining Technical Concepts Under Pressure

**Canonical chapter:** [`syllabus/20-interview-preparation/technical-answers/explaining-technical-concepts-under-pressure.md`](../syllabus/20-interview-preparation/technical-answers/explaining-technical-concepts-under-pressure.md)

## Card: The first move

**Prompt:**
If you change one thing about how you answer a technical question, what should it be?

**Answer:**
**Lead with the claim** — open with a single sentence that would be a complete, defensible answer if the interviewer stopped you immediately. Everything after it is support you control, rather than runway you need before you can land.

**Why it matters:**
Burying the claim forces the listener to hold unattached facts hoping they connect. Most stop trying around the fourth one.

**Common trap:**
Building toward the answer, which is the natural instinct of someone who knows the topic well.

**Related:**
[Five Moves That Do the Work](../syllabus/20-interview-preparation/technical-answers/explaining-technical-concepts-under-pressure.md#five-moves-that-do-the-work)

## Card: Why a re-read does not fix a bad explanation

**Prompt:**
You knew the answer and delivered it badly. Why is re-reading the chapter the wrong fix?

**Answer:**
Because the failure is **linearisation**, not knowledge. Knowing is simultaneous — the whole picture is available at once. Speech is serial: roughly two words per second, one order, no going back. Re-reading improves the picture you already had and does nothing for the translation. Sixty seconds out loud, recorded, improves both.

**Why it matters:**
This is why the failure survives months of otherwise good preparation: the practice being done trains the wrong skill and feels productive.

**Common trap:**
Measuring preparation in chapters read rather than answers delivered out loud.

**Related:**
[Level 1 — Foundation](../syllabus/20-interview-preparation/technical-answers/explaining-technical-concepts-under-pressure.md#level-1-foundation)

## Card: What a "10-Minute Deep Dive" actually contains

**Prompt:**
Measured across this repository, how much spoken material is in the median "10-Minute Deep Dive" section?

**Answer:**
**42 seconds** — one tenth of the claimed budget at 140 words per minute. They are outlines ("Cover, in order: A; B; C"), and good ones, but they are tables of contents rather than answers. By contrast the 30-second answers are well calibrated, with a median of exactly 30 seconds.

**Why it matters:**
If you prepared by reading a deep dive aloud, you produced 42 seconds of speech and have nothing rehearsed for the remaining nine minutes. That is the mechanical reason an answer opens strongly and thins out under follow-ups.

**Common trap:**
Treating the outline as the finished answer. The expansion happens out loud, in advance, and is the actual work.

**Related:**
[The Measurement That Explains the Failure](../syllabus/20-interview-preparation/technical-answers/explaining-technical-concepts-under-pressure.md#the-measurement-that-explains-the-failure)

## Card: When does an analogy help?

**Prompt:**
What makes a technical analogy usable, and what should you do with it unprompted?

**Answer:**
It must share the **mechanism**, not the category. "An `ArrayList` is a numbered row of parking spaces" works because contiguous-and-indexable explains both fast lookup and expensive middle insertion. "It's like a shopping list" fails: both are lists, and nothing about a shopping list explains the performance. Unprompted, **state where it breaks** — "a real car park can't silently resize itself, and that copy is exactly what capacity sizing is about" — which turns a liability into a credibility signal and moves you to real mechanism.

**Why it matters:**
A bad analogy is worse than none: it is a new thing you now have to defend.

**Common trap:**
Using two analogies, or riding one past its usefulness instead of switching to the real thing.

**Related:**
[Building an Analogy That Survives a Follow-Up](../syllabus/20-interview-preparation/technical-answers/explaining-technical-concepts-under-pressure.md#building-an-analogy-that-survives-a-follow-up)

## Card: The word is gone mid-answer

**Prompt:**
You know the concept but the term will not come to you. What do you do?

**Answer:**
Describe the mechanism and keep moving — do not stop to retrieve the label. *"There's a term for this and it's not coming to me — it's the thing where the collection throws if you modify it while iterating, because the iterator tracks a modification count."* You have demonstrated the knowledge, which is what is being assessed; nobody scores the retrieval speed of a class name.

**Why it matters:**
Stopping to hunt for a word converts a small gap into visible floundering, and the recovery costs more than the word was worth.

**Common trap:**
Restarting the whole answer after a small stumble, instead of correcting in one clause and continuing.

**Related:**
[When You Know It and It Will Not Come Out](../syllabus/20-interview-preparation/technical-answers/explaining-technical-concepts-under-pressure.md#when-you-know-it-and-it-will-not-come-out)
