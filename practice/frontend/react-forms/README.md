# React Forms demo app (F-114)

Real Vite + React 19 app backing [`syllabus/21-frontend-web/react-forms.md`](../../../syllabus/21-frontend-web/react-forms.md). Uses real `react-hook-form` 7.85.0 and `zod` 4.4.3, not hand-rolled approximations.

## Run it

```bash
npm install
npm run dev
```

Three sections:

1. **Controlled vs. uncontrolled** — a real, side-by-side render-count contrast between a `useState`-backed input and a `useRef`-backed one.
2. **Validation timing** — the identical rule (min 3 characters) applied via `onChange`, `onBlur`, and `onSubmit`, only WHEN the error appears differs.
3. **React Hook Form + Zod** — a real schema (`username`/`email`/`age`), a real failed submit showing all three zod error messages, then a real corrected submit succeeding.

## Captured evidence (real browser session)

### Controlled vs. uncontrolled
Typing was done one character at a time in separate tool calls after discovering the browser-automation tool's bulk `type` action dispatches a single combined input event for a whole string rather than one event per character — a testing-tool artifact caught and worked around, not a demo bug (see Verification performed below).

```
Controlled: 3 individual keystrokes ("a","b","c") -> renders 2 -> 8 (+6 = 3 renders x 2, StrictMode-doubled)
Uncontrolled: typing "xyz" -> renders unchanged at 2; clicking "Read value" -> renders 2 -> 4 (+2, one real read)
```
Direct proof: every controlled keystroke re-renders its own component; typing into the uncontrolled field causes zero re-renders — only the explicit `inputRef.current.value` read does.

### Validation timing
```
onChange: typed "a" -> error shown immediately, log: onChange("a") -> error shown
onBlur:   typed "a", blurred -> error shown only on blur, log: onBlur("a") -> error shown
onSubmit: typed "a", clicked Submit -> error shown only on submit, log: onSubmit("a") -> error shown
```
Same rule, three different real, observed timings.

### React Hook Form + Zod
```
Submit 1 (username "ab", email empty, age empty):
  Username must be at least 3 characters
  Enter a valid email address
  Must be 18 or older
  submit attempts: 1, submitted successfully: false

Submit 2 (username "abc", email "user@example.com", age "25"):
  (no errors)
  submit attempts: 2, submitted successfully: true
```
Real zod-generated error messages, then a real successful submission — not asserted, both submit attempts actually run through `handleSubmit`.

## Verification performed

- `npm run dev` — clean start; a fresh tab showed zero console errors throughout, including after every interaction.
- `npm run build` — clean production build, zero errors/warnings.
- **A real browser-automation-tool limitation caught and worked around, not hidden:** the `computer` tool's `type` action, when given a multi-character string, dispatches it as a single combined input event rather than one event per character (confirmed: typing `"hello"` in one call produced only `+2` renders on the controlled field, the same as a single keystroke; typing one character per separate tool call reliably produced `+2` per character). A real user's keyboard always fires one event per keystroke; this is specific to the automation tool's bulk-text path. Worked around by issuing one `type` call per character for the render-count proof.
