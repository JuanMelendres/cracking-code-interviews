---
title: "Cheat Sheet: How the Web Works (HTML, CSS, the DOM, and HTTP)"
slug: how-the-web-works-html-css-dom-and-http
document_type: cheat-sheet
domain: frontend
topic_id: F-001
tier: Beginner
canonical: ../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md
last_updated: 2026-09-08
---

# How the Web Works (HTML, CSS, the DOM, and HTTP)

**Canonical chapter:** [`syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md`](../syllabus/21-frontend-web/how-the-web-works-html-css-dom-and-http.md)

## Core Mental Model

A web page is the result of four separate stages: fetch bytes (HTTP, over an already-established TCP connection), parse them into two trees (HTML → DOM, CSS → cascade-resolved styles), combine into boxes with computed layout, paint pixels. HTML is not "the page" — it's the input to building the DOM, which *is* the live, in-memory page a browser and JavaScript actually work with.

## Essential Definitions

- **Element/tag/attribute** — `<a href="...">`: `a` is the element, `<a>`/`</a>` are tags, `href` is an attribute.
- **The DOM** — a live tree of JS objects built by parsing HTML; diverges from the original HTML text the moment any JavaScript mutates it. View Source shows the text; DevTools' Elements panel shows the live DOM.
- **Box model** — content → padding → border → margin, innermost to outermost. `box-sizing: border-box` makes declared width include padding+border (default `content-box` does not).
- **Cascade** — when rules conflict: `!important` > specificity (ID > class > element) > source order (later wins on a tie).
- **HTTP exchange** — request line + headers + optional body, answered by status line + headers + optional body. `Content-Type` says how to parse the body; `Content-Length` says how many bytes it is (needed because TCP has no message boundaries of its own).

## Decision Table

| Situation | Choice |
|---|---|
| Markup has real structural/navigational meaning (nav, main content, an article) | Semantic element (`<nav>`, `<main>`, `<article>`, `<header>`, `<footer>`) |
| Markup is a pure styling hook with no semantic meaning | `<div>`/`<span>` — the correct, intended use |
| Layout problem is "arrange items along one line, handle wrapping" | Flexbox (one-dimensional) |
| Layout problem needs rows AND columns to align simultaneously | Grid (two-dimensional) |
| A style isn't applying | Check specificity conflicts first, then typos, then confirm the selector actually matches — inspect live in DevTools |
| Debugging an HTTP-shaped bug | Capture a real `curl -v` transcript before guessing which layer is at fault |

## Common Pitfalls

- Building "div soup" — no semantic elements anywhere — which can pass every visual QA check and still fail an accessibility/SEO review outright.
- Not knowing padding/border are added *outside* a declared width by default (`content-box`), producing layouts wider than expected until `box-sizing: border-box` is set.
- Reaching for `!important` as a first fix instead of understanding the specificity conflict it papers over.
- Treating Flexbox and Grid as interchangeable rather than as one-dimensional vs. two-dimensional tools.
- Believing "View Page Source" reflects the current page state — it only ever shows the original HTML text, never DOM mutations made by JavaScript.
- Treating `Content-Type`/`Content-Length` as decorative metadata rather than the actual mechanism a browser uses to parse and frame a response.

## Interview Answer Skeleton

**30-sec:** HTML structures content, parsed into the DOM (a live tree, not the HTML text itself). CSS presents that structure via the cascade, resolved into a box model (content/padding/border/margin). JavaScript reads/mutates the DOM directly — what React abstracts away. HTTP is the plain-text request/response protocol moving all of this over an already-established TCP connection.

**2-min:** Walk the page-load sequence (DNS → TCP handshake → optional TLS → HTTP request/response → parse → style/layout → paint), note the DOM's independence from HTML source text, then land on why semantic HTML matters concretely (accessibility landmarks, SEO signal) rather than as style preference.

**Whiteboard:** Four boxes: "HTTP (fetch bytes)" → "Parse (HTML→DOM, CSS→styles)" → "Layout (compute boxes)" → "Paint (pixels)." Inset a DNS→TCP→HTTP mini-sequence above the first box; inset a four-layer box-model diagram (content/padding/border/margin) below the second.

**Senior-level framing (relative to this chapter's beginner scope):** States the DOM/HTML-text distinction and the box-sizing gotcha unprompted; can trace the full page-load sequence without prompting.

## Common Interview Traps

- Using "HTML" and "the DOM" as interchangeable terms.
- Being unable to say why `Content-Length` exists (TCP has no built-in message boundaries).
- Recommending Grid or Flexbox by habit rather than by the one-vs-two-dimensional nature of the actual layout problem.
- Assuming a page "looks right" implies it's semantically/structurally correct.

## Related

- `syllabus/21-frontend-web/react-fundamentals-jsx-components-props-and-state.md`
- `syllabus/01-computer-science-foundations/networking-basics.md`
- `syllabus/07-api-design/rest-api-fundamentals.md`
- `00-project/frontend-topic-register.md`
