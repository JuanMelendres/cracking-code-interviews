import { useState, useReducer } from 'react';

// F-109a: a real bug class useReducer structurally prevents. With several
// useState calls for related fields, "reset everything" means manually
// listing every field in the reset handler -- forget one (e.g. when a new
// field is added later by someone unfamiliar with the reset code) and reset
// silently leaves stale data behind. useReducer's reset is just "return
// initialState" -- adding a field to initialState makes it automatically
// covered by every existing RESET action, with no separate list to remember.

// ---- Buggy: several useState calls, reset handler lists fields manually ----
function StateBasedForm() {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  // `phone` added later, by someone who didn't know to update handleReset below.
  const [phone, setPhone] = useState('');

  function handleReset() {
    setName('');
    setEmail('');
    // BUG: whoever added `phone` above forgot to add setPhone('') here.
  }

  function fillSampleData() {
    setName('Ana');
    setEmail('ana@example.com');
    setPhone('555-0100');
  }

  return (
    <div>
      <strong>useState version (reset handler lists fields manually):</strong>
      <div>name: <span data-testid="state-name">"{name}"</span></div>
      <div>email: <span data-testid="state-email">"{email}"</span></div>
      <div>phone: <span data-testid="state-phone">"{phone}"</span></div>
      <button type="button" onClick={fillSampleData}>Fill sample data</button>
      <button type="button" onClick={handleReset}>Reset</button>
    </div>
  );
}

// ---- Fixed: useReducer, reset is just "return initialState" ----
const initialFormState = { name: '', email: '', phone: '' };

function formReducer(state, action) {
  switch (action.type) {
    case 'FILL_SAMPLE':
      return { name: 'Ana', email: 'ana@example.com', phone: '555-0100' };
    case 'RESET':
      return initialFormState; // covers EVERY field in initialFormState, automatically
    default:
      return state;
  }
}

function ReducerBasedForm() {
  const [form, dispatch] = useReducer(formReducer, initialFormState);

  return (
    <div>
      <strong>useReducer version (reset is just "return initialState"):</strong>
      <div>name: <span data-testid="reducer-name">"{form.name}"</span></div>
      <div>email: <span data-testid="reducer-email">"{form.email}"</span></div>
      <div>phone: <span data-testid="reducer-phone">"{form.phone}"</span></div>
      <button type="button" onClick={() => dispatch({ type: 'FILL_SAMPLE' })}>Fill sample data</button>
      <button type="button" onClick={() => dispatch({ type: 'RESET' })}>Reset</button>
    </div>
  );
}

export default function FormResetBugDemo() {
  return (
    <div className="demo-block">
      <h3>F-109a: a real reset bug — useState forgets a field, useReducer can't</h3>
      <p>Click "Fill sample data" then "Reset" on both. Watch the `phone` field.</p>
      <StateBasedForm />
      <ReducerBasedForm />
    </div>
  );
}
