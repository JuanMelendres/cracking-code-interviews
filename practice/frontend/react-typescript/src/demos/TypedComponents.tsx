import { useReducer, useRef, useState } from 'react';
import type { ReactNode } from 'react';

// Typed props: a required primitive, an optional callback, and children
// typed as ReactNode (not JSX.Element — ReactNode also covers strings,
// numbers, fragments, arrays, and null/undefined).
interface CardProps {
  title: string;
  onDismiss?: () => void;
  children?: ReactNode;
}

function Card({ title, onDismiss, children }: CardProps) {
  return (
    <div className="card">
      <h3>{title}</h3>
      {children}
      {onDismiss && (
        <button type="button" onClick={onDismiss}>
          Dismiss
        </button>
      )}
    </div>
  );
}

// Typed action union for useReducer, with an exhaustiveness-checked
// switch. If a new action type is added to CounterAction without a
// matching case here, the `default: return assertNever(action)` line
// fails to compile — a real, compile-time-caught bug, not a runtime one.
type CounterAction = { type: 'increment' } | { type: 'decrement' } | { type: 'reset'; to: number };

function assertNever(value: never): never {
  throw new Error(`Unhandled action: ${JSON.stringify(value)}`);
}

function counterReducer(state: number, action: CounterAction): number {
  switch (action.type) {
    case 'increment':
      return state + 1;
    case 'decrement':
      return state - 1;
    case 'reset':
      return action.to;
    default:
      return assertNever(action);
  }
}

export default function TypedComponents() {
  // useState's generic is inferred from the initial value here (number);
  // an explicit <number> would be needed if the initial value couldn't
  // convey the full type (e.g. starting at null but later holding a number).
  const [count, dispatch] = useReducer(counterReducer, 0);
  const [name, setName] = useState('');
  const inputRef = useRef<HTMLInputElement>(null);

  return (
    <div>
      <Card title="Typed props + children">
        <p>Hello, {name || 'stranger'}.</p>
        <input
          ref={inputRef}
          value={name}
          onChange={(event) => setName(event.target.value)}
          placeholder="Type your name"
        />
      </Card>

      <Card title="Typed useReducer with exhaustiveness checking" onDismiss={() => dispatch({ type: 'reset', to: 0 })}>
        <p data-testid="count">Count: {count}</p>
        <button type="button" onClick={() => dispatch({ type: 'increment' })}>
          +1
        </button>
        <button type="button" onClick={() => dispatch({ type: 'decrement' })}>
          -1
        </button>
      </Card>
    </div>
  );
}
