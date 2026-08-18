import { useState } from 'react';

// Real, side-by-side contrast: a genuine <button> vs. a <div onClick>
// "button". Both look identical (same CSS class) and both work with a
// mouse click — the difference only shows up with a keyboard, which is
// exactly why this class of bug survives so many manual QA passes.
export default function SemanticVsDivButtonDemo() {
  const [buttonCount, setButtonCount] = useState(0);
  const [divCount, setDivCount] = useState(0);

  return (
    <section className="demo">
      <h2>1. Semantic HTML vs. div-soup — real keyboard-access contrast</h2>
      <div className="row">
        <button className="fake-btn" onClick={() => setButtonCount((c) => c + 1)}>
          Real &lt;button&gt;
        </button>
        <p data-testid="button-count">clicks: {buttonCount}</p>
      </div>
      <div className="row">
        {/* eslint-disable-next-line jsx-a11y/no-static-element-interactions, jsx-a11y/click-events-have-key-events */}
        <div className="fake-btn" onClick={() => setDivCount((c) => c + 1)}>
          Fake &lt;div onClick&gt; button
        </div>
        <p data-testid="div-count">clicks: {divCount}</p>
      </div>
      <p>
        Tab through this page with a keyboard — the real button receives focus and can be
        activated with Enter/Space; the div never receives focus at all (no{' '}
        <code>tabIndex</code>, no <code>role="button"</code>, no key handler), and is silently
        unreachable to a keyboard-only or screen-reader user despite working perfectly for a
        mouse user.
      </p>
    </section>
  );
}
