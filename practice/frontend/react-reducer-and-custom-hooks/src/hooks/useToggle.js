import { useState, useCallback } from 'react';

// F-110a: the simplest possible custom hook -- just a function starting with
// `use` that itself calls other hooks. Naming convention (`use` prefix) is
// not decorative: it's how React's linter and React itself recognize this
// as a hook subject to the Rules of Hooks (can't be called conditionally,
// etc.). Each CALL SITE of useToggle gets its own independent state, exactly
// like any other hook -- proven in UseToggleDemo.jsx by two independent uses.
export function useToggle(initialValue = false) {
  const [value, setValue] = useState(initialValue);
  const toggle = useCallback(() => setValue((v) => !v), []);
  return [value, toggle];
}
