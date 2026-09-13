// Deliberately isolated in its own module so it becomes its own chunk
// under React.lazy(() => import(...)) — real code-splitting, not
// simulated.
export default function HeavyPanel() {
  return (
    <div data-testid="heavy-panel">
      <p>Heavy panel loaded (this code lived in a separate JS chunk until now).</p>
    </div>
  );
}
