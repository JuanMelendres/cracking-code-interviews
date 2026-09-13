import { useState, useTransition, useEffect, useRef, useMemo } from 'react';

const ALL_ITEMS = Array.from({ length: 20000 }, (_, i) => `item-${i}-${(i * 7919) % 100000}`);

// Artificial synchronous cost so the filter is heavy enough to observe
// isPending staying true across more than one commit — real production
// filters over large lists (or expensive derived computations) behave the
// same way; this loop just makes the cost reproducible on any machine.
function expensiveFilter(query) {
  let acc = 0;
  for (let i = 0; i < 4_000_000; i++) acc += i % 7;
  if (!query) return ALL_ITEMS.slice(0, 20);
  return ALL_ITEMS.filter((item) => item.includes(query)).slice(0, 20);
}

export default function TransitionDemo() {
  const [inputValue, setInputValue] = useState('');
  const [query, setQuery] = useState('');
  const [isPending, startTransition] = useTransition();
  const [pendingLog, setPendingLog] = useState([]);
  const prevPendingRef = useRef(false);

  useEffect(() => {
    if (isPending !== prevPendingRef.current) {
      prevPendingRef.current = isPending;
      setPendingLog((log) => [...log, `pending: ${isPending}`]);
    }
  }, [isPending]);

  const results = useMemo(() => expensiveFilter(query), [query]);

  function handleChange(e) {
    const value = e.target.value;
    setInputValue(value); // urgent: input must echo keystrokes immediately
    startTransition(() => {
      setQuery(value); // low-priority: can be interrupted/deferred
    });
  }

  return (
    <section className="demo">
      <h2>1. useTransition — urgent input vs. deferred list</h2>
      <input
        aria-label="transition-search"
        value={inputValue}
        onChange={handleChange}
        placeholder="type to filter 20,000 items"
      />
      <p data-testid="transition-pending">isPending: {String(isPending)}</p>
      <p data-testid="transition-result-count">results: {results.length}</p>
      <p data-testid="transition-pending-log">pending log: {pendingLog.join(' -> ') || '(none yet)'}</p>
      <ul>
        {results.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>
    </section>
  );
}
