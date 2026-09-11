---
title: "Flashcards: MethodHandle and java.lang.invoke"
slug: methodhandle-and-invoke
document_type: flashcard-deck
domain: 02-java/concurrency
topic_id: T-2405
canonical: ../syllabus/02-java/concurrency/methodhandle-and-invoke.md
last_updated: 2026-09-11
---

# Flashcards: MethodHandle and java.lang.invoke

**Canonical chapter:** [`syllabus/02-java/concurrency/methodhandle-and-invoke.md`](../syllabus/02-java/concurrency/methodhandle-and-invoke.md)

## Card: invokeExact vs. invoke

**Prompt:**
What's the real difference between `handle.invokeExact(args)` and `handle.invoke(args)`?

**Answer:**
`invokeExact()` requires the arguments to match the handle's `MethodType` exactly, throwing a real `WrongMethodTypeException` on mismatch. `invoke()` adapts automatically (boxing, widening) to make the call work.

**Why it matters:**
A concrete API distinction interviewers use to check real hands-on experience versus surface familiarity.

**Common trap:**
Assuming both methods behave identically, just with different names.

**Related:**
[MethodHandle and java.lang.invoke](../syllabus/02-java/concurrency/methodhandle-and-invoke.md)

## Card: What a lambda actually compiles to

**Prompt:**
Does a Java lambda expression produce an extra `.class` file, like an anonymous inner class does?

**Answer:**
No — a real `javap -v` disassembly shows the lambda compiles to an `invokedynamic` call site bootstrapped via `LambdaMetafactory`, producing zero extra class files. The equivalent anonymous inner class produces a real, extra `$1.class` file.

**Why it matters:**
A concrete, verifiable fact distinguishing lambdas from anonymous inner classes beyond syntax sugar.

**Common trap:**
Assuming a lambda is "just sugar" for an anonymous inner class with identical compiled output.

**Related:**
[MethodHandle and java.lang.invoke](../syllabus/02-java/concurrency/methodhandle-and-invoke.md)

## Card: bindTo() doesn't mutate

**Prompt:**
Does `handle.bindTo(receiver)` modify `handle` itself?

**Answer:**
No — it returns a genuinely new `MethodHandle` with a reduced `MethodType` (the receiver parameter removed), leaving the original handle unchanged.

**Why it matters:**
`MethodHandle`s are immutable value-like objects; combinators always produce new handles.

**Common trap:**
Assuming `bindTo()` mutates the handle in place, the way a builder pattern method might.

**Related:**
[MethodHandle and java.lang.invoke](../syllabus/02-java/concurrency/methodhandle-and-invoke.md)
