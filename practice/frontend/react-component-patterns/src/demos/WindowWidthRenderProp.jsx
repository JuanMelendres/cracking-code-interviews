import { useState, useEffect } from 'react';

// F-111b: a render prop -- a component that takes a FUNCTION as its
// `children` (or any prop), calls that function with its internal state,
// and renders whatever the function returns. Same "share stateful
// behavior" goal as the HOC, different shape: no new component gets
// created and mounted in the tree on your behalf, and the caller sees the
// value explicitly at the call site instead of via an injected prop.
export function WindowWidth({ children }) {
  const [width, setWidth] = useState(window.innerWidth);

  useEffect(() => {
    function handleResize() {
      setWidth(window.innerWidth);
    }
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return children(width);
}
