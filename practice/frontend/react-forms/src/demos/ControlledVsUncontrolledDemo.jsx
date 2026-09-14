import { useRef, useState } from 'react';

function ControlledField() {
  const [value, setValue] = useState('');
  const renderCount = useRef(0);
  renderCount.current += 1;

  return (
    <div className="field">
      <label>
        Controlled (useState)
        <input
          data-testid="controlled-input"
          value={value}
          onChange={(e) => setValue(e.target.value)}
        />
      </label>
      <p data-testid="controlled-render-count">renders: {renderCount.current}</p>
      <p data-testid="controlled-value">value: {value}</p>
    </div>
  );
}

function UncontrolledField() {
  const inputRef = useRef(null);
  const renderCount = useRef(0);
  const [lastSubmitted, setLastSubmitted] = useState('');
  renderCount.current += 1;

  return (
    <div className="field">
      <label>
        Uncontrolled (useRef + defaultValue)
        <input data-testid="uncontrolled-input" ref={inputRef} defaultValue="" />
      </label>
      <button type="button" onClick={() => setLastSubmitted(inputRef.current.value)}>
        Read value
      </button>
      <p data-testid="uncontrolled-render-count">renders: {renderCount.current}</p>
      <p data-testid="uncontrolled-value">last read value: {lastSubmitted}</p>
    </div>
  );
}

// Real, side-by-side render-count proof: typing into the controlled field
// re-renders ITS OWN component on every keystroke (React must re-render to
// reflect the new `value` prop back into the DOM); typing into the
// uncontrolled field never re-renders anything — the DOM node itself holds
// the value until something explicitly reads `inputRef.current.value`.
export default function ControlledVsUncontrolledDemo() {
  return (
    <section className="demo">
      <h2>1. Controlled vs. uncontrolled — real render-count contrast</h2>
      <ControlledField />
      <UncontrolledField />
    </section>
  );
}
