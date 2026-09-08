---
title: "Flashcards: JavaScript Fundamentals (Variables, Functions, Objects, Closures, and Asynchrony)"
slug: javascript-fundamentals-variables-functions-and-asynchrony
document_type: flashcard-deck
domain: frontend
topic_id: F-002
tier: Beginner
canonical: ../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md
last_updated: 2026-09-08
---

# Flashcards: JavaScript Fundamentals (Variables, Functions, Objects, Closures, and Asynchrony)

**Canonical chapter:** [`syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md`](../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md)

## Card: this in arrow vs. regular functions

**Prompt:**
Why does a regular function passed as a `setTimeout` callback inside an object method lose access to the object as `this`, while an arrow function doesn't?

**Answer:**
A regular function's `this` is determined by how it's called — `setTimeout` calls the callback as a plain function, not as `object.method()`, so `this` is not the object. An arrow function has no `this` of its own; it inherits `this` lexically from the enclosing method, which was correctly called as `object.method()`.

**Why it matters:**
One of the most common real JavaScript bugs, and a frequent live-coding interview trap.

**Common trap:**
Believing `this` "belongs to" the object a method is defined on, rather than being determined by the call site for regular functions.

**Related:**
[JavaScript Fundamentals: Variables, Functions, Objects, Closures, and Asynchrony](../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md)

## Card: Event loop ordering

**Prompt:**
Given synchronous code, a `.then()` callback, and a `setTimeout(fn, 0)`, all interleaved in source order, what actually prints first?

**Answer:**
All synchronous code first, then the `.then()` callback (a microtask), then the `setTimeout` callback (a macrotask) — regardless of source order, because the event loop always fully drains the microtask queue before running even one macrotask.

**Why it matters:**
Explains why `setTimeout(fn, 0)` never runs "immediately," and is the mechanical basis for understanding `async`/`await` ordering.

**Common trap:**
Assuming source order determines execution order for anything asynchronous.

**Related:**
[JavaScript Fundamentals: Variables, Functions, Objects, Closures, and Asynchrony](../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md)

## Card: What a closure actually is

**Prompt:**
What is a closure, precisely — not just "a function inside a function"?

**Answer:**
A function that retains access to variables from its enclosing scope even after that enclosing function has already returned. The defining property is that access, not the nesting itself.

**Why it matters:**
This is the literal mechanism behind React's `useState` and any factory function that returns multiple functions sharing private state.

**Common trap:**
Defining a closure only by its shape ("a nested function") without the actual behavior (surviving access after the outer scope returns).

**Related:**
[JavaScript Fundamentals: Variables, Functions, Objects, Closures, and Asynchrony](../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md)

## Card: == vs === and [] == false

**Prompt:**
Why does `[] == false` evaluate to `true` in JavaScript?

**Answer:**
`==` coerces both operands toward a common type before comparing. `[]` coerces to `''` (empty string) and then to the number `0`; `false` coerces to `0`. `0 == 0` is `true`. `===` never coerces, so `[] === false` is `false`.

**Why it matters:**
A concrete, memorable justification for why this repository always recommends `===`.

**Common trap:**
Treating `==` and `===` as stylistically interchangeable.

**Related:**
[JavaScript Fundamentals: Variables, Functions, Objects, Closures, and Asynchrony](../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md)

## Card: const does not mean immutable

**Prompt:**
Does `const` prevent an object's properties from being changed?

**Answer:**
No — `const` only prevents reassigning the binding itself (`obj = somethingElse` is an error). Mutating the object's properties (`obj.field = newValue`, or `arr.push(x)`) is completely legal.

**Why it matters:**
A very common early misunderstanding that leads to confusion about why "immutable" `const` data still appears to change.

**Common trap:**
Believing `const` provides deep immutability rather than binding-only immutability.

**Related:**
[JavaScript Fundamentals: Variables, Functions, Objects, Closures, and Asynchrony](../syllabus/21-frontend-web/javascript-fundamentals-variables-functions-and-asynchrony.md)
