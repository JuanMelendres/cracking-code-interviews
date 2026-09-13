import { createContext, useContext, useRef, useState } from 'react';

// One Context holding TWO independent slices (count and name) in a
// single value object. This is the realistic case — most real Context
// usages bundle more than one piece of state — and it's exactly what
// produces the re-render-fan-out this demo measures.
const StoreContext = createContext(null);

function StoreProvider({ children }) {
  const [count, setCount] = useState(0);
  const [name, setName] = useState('anon');
  return (
    <StoreContext.Provider value={{ count, setCount, name, setName }}>
      {children}
    </StoreContext.Provider>
  );
}

function CountConsumer() {
  const { count, setCount } = useContext(StoreContext);
  const renderCount = useRef(0);
  renderCount.current += 1;
  return (
    <p data-testid="context-count-consumer">
      Count: {count} (renders: {renderCount.current})
      <button type="button" onClick={() => setCount((c) => c + 1)}>
        +1 count
      </button>
    </p>
  );
}

// Reads ONLY `name` — never touches `count` — but still re-renders
// every time `count` changes, because Context has no concept of
// "which field did this consumer actually read." Any change to the
// provider's value re-renders every consumer of that context.
function NameConsumer() {
  const { name } = useContext(StoreContext);
  const renderCount = useRef(0);
  renderCount.current += 1;
  return (
    <p data-testid="context-name-consumer">
      Name: {name} (renders: {renderCount.current})
    </p>
  );
}

export default function ContextDemo() {
  return (
    <StoreProvider>
      <CountConsumer />
      <NameConsumer />
    </StoreProvider>
  );
}
