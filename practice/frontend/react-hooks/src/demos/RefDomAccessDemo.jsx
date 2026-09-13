import { useRef } from 'react';

// F-106a: useRef's most common use -- a handle to a real DOM node. React
// attaches the actual <input> element to `inputRef.current` after mount;
// calling `.focus()` on it is a genuine imperative DOM API call, not
// something React's declarative model can express any other way.

export default function RefDomAccessDemo() {
  const inputRef = useRef(null);

  return (
    <div className="demo-block">
      <h3>F-106a: useRef for direct DOM access</h3>
      <input ref={inputRef} type="text" placeholder="click the button to focus me" data-testid="ref-target-input" />
      <button type="button" onClick={() => inputRef.current.focus()}>
        Focus the input
      </button>
    </div>
  );
}
