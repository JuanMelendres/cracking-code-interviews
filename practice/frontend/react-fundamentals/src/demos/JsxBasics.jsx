// F-101: JSX is not HTML -- it compiles to React.createElement() calls (or, with
// the modern automatic JSX runtime Vite uses, jsx()/jsxs() calls from
// react/jsx-runtime). React then builds a lightweight in-memory tree from those
// calls (the "virtual DOM") and diffs it against the previous tree to compute
// the minimal set of real DOM mutations -- it does NOT re-render the whole page.

function withoutJsx() {
  // This is what the JSX below actually becomes after compilation.
  // Written by hand here so the equivalence is visible, not asserted.
  return React_createElement_equivalent();
}

function React_createElement_equivalent() {
  return {
    type: 'p',
    props: { className: 'plain-object', children: 'Built without JSX syntax' },
  };
}

export default function JsxBasics() {
  const withJsx = <p className="plain-object">Built WITH JSX syntax</p>;
  const manual = withoutJsx();

  return (
    <div className="demo-block">
      <h3>F-101: JSX vs. the plain object it compiles to</h3>
      <p>
        The JSX element below and the hand-built object below it both describe
        the same thing: a plain JS object with a <code>type</code> and{' '}
        <code>props</code>. Neither is a DOM node yet -- React turns this
        description into real DOM nodes, then reuses/patches them on
        re-render instead of throwing them away.
      </p>
      {withJsx}
      <pre>{JSON.stringify(manual, null, 2)}</pre>
    </div>
  );
}
