---
title: "Flashcards: Working with Legacy Code"
slug: working-with-legacy-code
document_type: flashcard-deck
domain: 18-engineering-practices
topic_id: T-1803
canonical: ../syllabus/18-engineering-practices/working-with-legacy-code.md
last_updated: 2026-09-07
---

# Flashcards: Working with Legacy Code

**Canonical chapter:** [`syllabus/18-engineering-practices/working-with-legacy-code.md`](../syllabus/18-engineering-practices/working-with-legacy-code.md)

## Card: The actionable definition of "legacy code"

**Prompt:**
What does "legacy code" mean in Michael Feathers' definition, as used in this chapter?

**Answer:**
Code without tests — regardless of its age, how well-written it otherwise is, or who wrote it. Not "old code," not "code someone else wrote."

**Why it matters:**
It's specific and actionable: code without tests is legacy precisely because you cannot mechanically know whether a change preserved its existing behavior.

**Common trap:**
Assuming "legacy" refers to age or an outdated framework rather than the presence or absence of tests.

**Related:**
[syllabus/18-engineering-practices/working-with-legacy-code.md](../syllabus/18-engineering-practices/working-with-legacy-code.md)

## Card: What a characterization test actually captures

**Prompt:**
Does a characterization test assert what code *should* do, or what it *actually does*?

**Answer:**
What it actually does, right now, as observed by running it with real inputs — including any surprising or seemingly-wrong behavior. It makes no claim about correctness.

**Why it matters:**
This is what distinguishes it from a normal unit test: a characterization test pins down current behavior as a regression-detecting safety net, not a specification of desired behavior.

**Common trap:**
Treating a passing characterization suite as proof the code is correct, when it only proves current behavior is unchanged since the tests were written.

**Related:**
[syllabus/18-engineering-practices/working-with-legacy-code.md](../syllabus/18-engineering-practices/working-with-legacy-code.md)

## Card: Probe before asserting

**Prompt:**
What are the two steps of the characterization-testing workflow, in strict order, and why does the order matter?

**Answer:**
First, probe the code's actual behavior across realistic inputs, printing (not asserting) results. Second, turn every observed result into an explicit assertion. If you write assertions from what you *expect* instead of what you observed, a wrong expectation becomes a false assertion you might "fix" to match your assumption — silently defeating the technique.

**Why it matters:**
Probing first and asserting only on real, observed output removes an entire failure mode where the tests encode assumptions instead of reality.

**Common trap:**
Writing assertions based on assumed behavior before actually running the code.

**Related:**
[syllabus/18-engineering-practices/working-with-legacy-code.md](../syllabus/18-engineering-practices/working-with-legacy-code.md)

## Card: The discount-cliff finding

**Prompt:**
What surprising, real behavior did probing `LegacyOrderPricer.price()` across quantities and unit prices actually surface?

**Answer:**
At `unitPrice=19.99`, ordering 9 units and ordering 10 units both produce the identical total price of 179.91 — the bulk discount at the 10-unit threshold exactly offsets the cost of the extra unit. This was discovered by running the code, not anticipated beforehand.

**Why it matters:**
A characterization test pins this down as *real, current behavior* without endorsing it as correct — whether it's a bug worth fixing becomes a separate, deliberate decision instead of something an unrelated refactor could silently alter.

**Common trap:**
Treating a characterized quirk as either "obviously a bug to fix immediately" or "obviously fine" rather than a deliberate, separate decision.

**Related:**
[syllabus/18-engineering-practices/working-with-legacy-code.md](../syllabus/18-engineering-practices/working-with-legacy-code.md)

## Card: Seams enable testing without a risky upfront refactor

**Prompt:**
What is a "seam," in Michael Feathers' terminology?

**Answer:**
A place in the code where behavior can be changed without editing the code in that exact spot — a point where a dependency can be substituted, such as an interface, a parameter, or a subclass override point.

**Why it matters:**
Finding seams is what makes it possible to isolate legacy code well enough to test it at all, without first performing a large, risky restructure just to make it testable.

**Common trap:**
Assuming untested code must first be broadly refactored before any test can be written, when a small seam is often enough.

**Related:**
[syllabus/18-engineering-practices/working-with-legacy-code.md](../syllabus/18-engineering-practices/working-with-legacy-code.md)

## Card: Sprout method / sprout class

**Prompt:**
What's the lowest-risk way to add new behavior to a legacy method that has no tests?

**Answer:**
Rather than modifying the existing, untested method directly, write the new logic as a new, separately-testable method or class (a "sprout"), and call it from one minimal, easy-to-verify point in the existing code.

**Why it matters:**
This confines the risk of a new bug to new, testable code rather than mixing it into code with no safety net — though it defers, rather than eliminates, the existing method's test debt.

**Common trap:**
Editing the existing untested method in place to weave in new logic, risking an accidental behavior change with nothing to catch it.

**Related:**
[syllabus/18-engineering-practices/working-with-legacy-code.md](../syllabus/18-engineering-practices/working-with-legacy-code.md)
