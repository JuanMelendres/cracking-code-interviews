import { useEffect, useRef, useState } from 'react';

// A real, minimal focus trap: on open, focus moves INTO the modal; Tab/
// Shift+Tab are intercepted at the boundary elements to cycle within the
// modal instead of escaping to the page behind it; on close, focus is
// explicitly returned to the element that opened it — all three are
// real, separately verifiable behaviors, not a single "it's accessible"
// claim.
function Modal({ onClose }) {
  const firstFocusableRef = useRef(null);
  const lastFocusableRef = useRef(null);

  useEffect(() => {
    firstFocusableRef.current?.focus();
  }, []);

  function handleKeyDown(e) {
    if (e.key === 'Escape') {
      onClose();
      return;
    }
    if (e.key !== 'Tab') return;
    if (e.shiftKey && document.activeElement === firstFocusableRef.current) {
      e.preventDefault();
      lastFocusableRef.current?.focus();
    } else if (!e.shiftKey && document.activeElement === lastFocusableRef.current) {
      e.preventDefault();
      firstFocusableRef.current?.focus();
    }
  }

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="modal-title"
      className="modal-overlay"
      onKeyDown={handleKeyDown}
    >
      <div className="modal-content">
        <h3 id="modal-title">Modal dialog</h3>
        <input ref={firstFocusableRef} data-testid="modal-input" placeholder="first focusable" />
        <button ref={lastFocusableRef} data-testid="modal-close" onClick={onClose}>
          Close
        </button>
      </div>
    </div>
  );
}

export default function FocusTrapModalDemo() {
  const [isOpen, setIsOpen] = useState(false);
  const triggerRef = useRef(null);

  function handleClose() {
    setIsOpen(false);
    // Real focus return: without this, focus would silently fall back
    // to <body>, disorienting a keyboard/screen-reader user who now has
    // no idea where they are on the page.
    triggerRef.current?.focus();
  }

  return (
    <section className="demo">
      <h2>2. Focus trap + focus return — real modal, real behavior</h2>
      <button ref={triggerRef} data-testid="modal-trigger" onClick={() => setIsOpen(true)}>
        Open Modal
      </button>
      {isOpen && <Modal onClose={handleClose} />}
    </section>
  );
}
