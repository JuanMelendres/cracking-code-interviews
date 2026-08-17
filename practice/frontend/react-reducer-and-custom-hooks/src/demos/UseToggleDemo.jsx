import { useToggle } from '../hooks/useToggle';

// F-110a: two independent uses of the SAME custom hook, proving (again,
// consistent with F-103's useState-instance lesson) that a custom hook's
// state lives with the calling component INSTANCE, not with the hook
// function definition -- toggling one never affects the other.
function Panel({ label }) {
  const [isOpen, toggle] = useToggle(false);
  return (
    <div>
      <button type="button" onClick={toggle}>
        {label}: {isOpen ? 'OPEN' : 'CLOSED'}
      </button>
    </div>
  );
}

export default function UseToggleDemo() {
  return (
    <div className="demo-block">
      <h3>F-110a: a custom hook — useToggle, two independent instances</h3>
      <Panel label="Panel A" />
      <Panel label="Panel B" />
    </div>
  );
}
