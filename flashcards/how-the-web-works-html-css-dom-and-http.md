---
title: "Flashcards: How the Web Works (HTML, CSS, the DOM, and HTTP)"
slug: how-the-web-works-html-css-dom-and-http
document_type: flashcard-deck
domain: frontend
topic_id: F-001
tier: Beginner
canonical: ../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md
last_updated: 2026-09-08
---

# Flashcards: How the Web Works (HTML, CSS, the DOM, and HTTP)

**Canonical chapter:** [`syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md`](../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md)

## Card: DOM vs. HTML source

**Prompt:**
What's the difference between the HTML a server sends and the DOM?

**Answer:**
HTML is static text. The DOM is a live, in-memory tree of objects the browser builds by parsing that text — JavaScript can mutate the DOM directly, and those changes are never reflected back into the original HTML text. View Source shows the HTML; DevTools' Elements panel shows the live DOM.

**Why it matters:**
It's the exact distinction React's whole model (describe a tree, patch the real DOM) is built on top of.

**Common trap:**
Using "HTML" and "the DOM" interchangeably.

**Related:**
[How the Web Works: HTML, CSS, the DOM, and HTTP](../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md)

## Card: box-sizing: border-box

**Prompt:**
What does `box-sizing: border-box` change about how `width` is interpreted?

**Answer:**
By default, a declared `width` applies only to the content box — padding and border add on top, making the real footprint larger. `border-box` makes the declared width include padding and border, so adding either doesn't change the total size.

**Why it matters:**
The single most common "my layout is wider than I set it" bug for beginners.

**Common trap:**
Not knowing this is even a setting — assuming `width` always means total footprint.

**Related:**
[How the Web Works: HTML, CSS, the DOM, and HTTP](../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md)

## Card: Flexbox vs. Grid

**Prompt:**
When do you reach for Flexbox vs. Grid?

**Answer:**
Flexbox for one-dimensional problems (arranging items along a single row or column). Grid for two-dimensional problems (aligning content across rows AND columns simultaneously).

**Why it matters:**
Reaching for the wrong one either fights the model (Flexbox for a true grid) or adds unnecessary complexity (Grid for a simple line of items).

**Common trap:**
Treating them as interchangeable, or as "which one is newer/better."

**Related:**
[How the Web Works: HTML, CSS, the DOM, and HTTP](../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md)

## Card: Semantic HTML's real value

**Prompt:**
Beyond style, what does using `<nav>`/`<main>`/`<article>` instead of `<div>` actually buy you?

**Answer:**
Screen readers expose semantic elements as navigable landmarks (accessibility); search crawlers use them as a strong content-structure signal (SEO). A visually identical page built entirely from `<div>`s gets neither, for free or otherwise.

**Why it matters:**
A page can pass every visual QA check and still fail an accessibility audit outright.

**Common trap:**
Assuming "it looks right" means "it's structured right."

**Related:**
[How the Web Works: HTML, CSS, the DOM, and HTTP](../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md)

## Card: Why Content-Length exists

**Prompt:**
Why does an HTTP response need a `Content-Length` header at all?

**Answer:**
HTTP rides on top of TCP, which delivers a raw byte stream with no built-in concept of "where one message ends." `Content-Length` (or `Transfer-Encoding: chunked`) is how the response tells the client exactly how many body bytes to read before the message is complete.

**Why it matters:**
Without it, a client reading a reused TCP connection has no way to know where a response ends and the next one (if any) begins.

**Common trap:**
Treating `Content-Length` as decorative metadata rather than a required framing mechanism.

**Related:**
[How the Web Works: HTML, CSS, the DOM, and HTTP](../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md)
