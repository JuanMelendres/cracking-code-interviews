import { useState, useDeferredValue, useEffect, useRef, useMemo } from 'react';

const ALL_ITEMS = Array.from({ length: 20000 }, (_, i) => `entry-${i}-${(i * 104729) % 100000}`);

function expensiveFilter(query) {
  let acc = 0;
  for (let i = 0; i < 4_000_000; i++) acc += i % 5;
  if (!query) return ALL_ITEMS.slice(0, 20);
  return ALL_ITEMS.filter((item) => item.includes(query)).slice(0, 20);
}

export default function DeferredValueDemo() {
  const [query, setQuery] = useState('');
  const deferredQuery = useDeferredValue(query);
  const isStale = query !== deferredQuery;
  const [staleLog, setStaleLog] = useState([]);
  const prevStaleRef = useRef(false);

  useEffect(() => {
    if (isStale !== prevStaleRef.current) {
      prevStaleRef.current = isStale;
      setStaleLog((log) => [...log, `stale: ${isStale}`]);
    }
  }, [isStale]);

  const results = useMemo(() => expensiveFilter(deferredQuery), [deferredQuery]);

  return (
    <section className="demo">
      <h2>2. useDeferredValue — no explicit transition, value itself lags</h2>
      <input
        aria-label="deferred-search"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="type to filter 20,000 entries"
      />
      <p data-testid="deferred-query">query: {query}</p>
      <p data-testid="deferred-value">deferredQuery: {deferredQuery}</p>
      <p data-testid="deferred-stale">isStale: {String(isStale)}</p>
      <p data-testid="deferred-stale-log">stale log: {staleLog.join(' -> ') || '(none yet)'}</p>
      <ul>
        {results.map((item) => (
          <li key={item}>{item}</li>
        ))}
      </ul>
    </section>
  );
}
