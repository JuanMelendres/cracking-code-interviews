import { useState } from 'react';

const MIN_LENGTH = 3;

function validate(value) {
  return value.length >= MIN_LENGTH ? '' : `Must be at least ${MIN_LENGTH} characters`;
}

// Three independent fields, identical validation rule, three different
// TIMING strategies — the actual interview-relevant distinction isn't the
// rule itself, it's WHEN the error becomes visible relative to user input.
function TimedField({ strategy }) {
  const [value, setValue] = useState('');
  const [error, setError] = useState('');
  const [touched, setTouched] = useState(false);
  const [log, setLog] = useState([]);

  function appendLog(entry) {
    setLog((l) => [...l, entry]);
  }

  function handleChange(e) {
    const next = e.target.value;
    setValue(next);
    if (strategy === 'onChange') {
      const msg = validate(next);
      setError(msg);
      appendLog(`onChange("${next}") -> ${msg ? 'error shown' : 'no error'}`);
    }
  }

  function handleBlur() {
    setTouched(true);
    if (strategy === 'onBlur') {
      const msg = validate(value);
      setError(msg);
      appendLog(`onBlur("${value}") -> ${msg ? 'error shown' : 'no error'}`);
    }
  }

  function handleSubmit(e) {
    e.preventDefault();
    if (strategy === 'onSubmit') {
      const msg = validate(value);
      setError(msg);
      appendLog(`onSubmit("${value}") -> ${msg ? 'error shown' : 'no error'}`);
    }
  }

  return (
    <form className="field" onSubmit={handleSubmit}>
      <label>
        {strategy}
        <input
          data-testid={`${strategy}-input`}
          value={value}
          onChange={handleChange}
          onBlur={handleBlur}
        />
      </label>
      {strategy === 'onSubmit' && <button type="submit">Submit</button>}
      <p data-testid={`${strategy}-error`}>{error || '(no error shown)'}</p>
      <p data-testid={`${strategy}-log`}>log: {log.join(' | ') || '(none yet)'}</p>
      <p data-testid={`${strategy}-touched`}>touched: {String(touched)}</p>
    </form>
  );
}

export default function ValidationTimingDemo() {
  return (
    <section className="demo">
      <h2>2. Validation timing — onChange vs. onBlur vs. onSubmit</h2>
      <p>Same rule (min {MIN_LENGTH} chars) on all three; only WHEN the error appears differs.</p>
      <TimedField strategy="onChange" />
      <TimedField strategy="onBlur" />
      <TimedField strategy="onSubmit" />
    </section>
  );
}
