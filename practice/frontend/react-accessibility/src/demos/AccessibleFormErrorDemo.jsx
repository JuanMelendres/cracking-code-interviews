import { useState } from 'react';

const MIN_LENGTH = 3;
const ERROR_ID = 'username-error';

// Real ARIA error association: `aria-invalid` toggles based on actual
// validity, and `aria-describedby` points at the error message's real id
// — this is what lets a screen reader announce the error text when the
// field receives focus, not just show it visually for sighted users.
export default function AccessibleFormErrorDemo() {
  const [value, setValue] = useState('');
  const [touched, setTouched] = useState(false);
  const isInvalid = touched && value.length < MIN_LENGTH;

  return (
    <section className="demo">
      <h2>3. Accessible form error association — real ARIA wiring</h2>
      <label htmlFor="username-input">Username</label>
      <input
        id="username-input"
        data-testid="username-input"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onBlur={() => setTouched(true)}
        aria-invalid={isInvalid}
        aria-describedby={isInvalid ? ERROR_ID : undefined}
      />
      {isInvalid && (
        <p id={ERROR_ID} data-testid="username-error" role="alert">
          Username must be at least {MIN_LENGTH} characters
        </p>
      )}
      <p data-testid="aria-invalid-value">
        aria-invalid on the input: {String(isInvalid)}
      </p>
      <p data-testid="aria-describedby-value">
        aria-describedby on the input: {isInvalid ? ERROR_ID : '(not set)'}
      </p>
    </section>
  );
}
