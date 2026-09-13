import { createContext, useContext, useState } from 'react';

// F-108a: prop drilling vs. Context, side by side. Both paths deliver the
// SAME value (`theme`) to a deeply nested consumer -- the difference is
// purely structural: does every intermediate component have to know about
// and pass through a prop it never itself uses?

// ---- Path A: prop drilling ----
function DrillLevel1({ theme }) {
  return <DrillLevel2 theme={theme} />;
}
function DrillLevel2({ theme }) {
  return <DrillLevel3 theme={theme} />;
}
function DrillLevel3({ theme }) {
  return <span data-testid="drilled-value">Drilled theme: {theme}</span>;
}

// ---- Path B: Context ----
const ThemeContext = createContext(null);

function ContextLevel1() {
  return <ContextLevel2 />;
}
function ContextLevel2() {
  return <ContextLevel3 />;
}
function ContextLevel3() {
  const theme = useContext(ThemeContext);
  return <span data-testid="context-value">Context theme: {theme}</span>;
}

export default function PropDrillingVsContextDemo() {
  const [theme, setTheme] = useState('dark');

  return (
    <div className="demo-block">
      <h3>F-108a: prop drilling vs. Context — same value, different plumbing</h3>
      <button type="button" onClick={() => setTheme((t) => (t === 'dark' ? 'light' : 'dark'))}>
        Toggle theme (currently {theme})
      </button>
      <p>
        <strong>Path A (props):</strong> DrillLevel1/2/3 each receive and re-pass a `theme`
        prop they never use themselves, purely to reach DrillLevel3.
      </p>
      <DrillLevel1 theme={theme} />
      <p>
        <strong>Path B (Context):</strong> ContextLevel1/2 know nothing about `theme` at all;
        only ContextLevel3 calls <code>useContext(ThemeContext)</code>.
      </p>
      <ThemeContext.Provider value={theme}>
        <ContextLevel1 />
      </ThemeContext.Provider>
    </div>
  );
}
