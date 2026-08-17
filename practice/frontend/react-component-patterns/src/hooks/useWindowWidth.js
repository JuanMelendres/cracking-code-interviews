import { useState, useEffect } from 'react';

// F-111c: the modern, preferred way to share "window width" behavior across
// components -- a custom hook. No wrapper component, no indirection layer:
// the consuming component calls this directly and gets a plain value back.
export function useWindowWidth() {
  const [width, setWidth] = useState(window.innerWidth);

  useEffect(() => {
    function handleResize() {
      setWidth(window.innerWidth);
    }
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return width;
}
