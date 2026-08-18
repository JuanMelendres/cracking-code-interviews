import './App.css';
import ContextDemo from './demos/ContextDemo';
import ReduxDemo from './demos/ReduxDemo';
import ZustandDemo from './demos/ZustandDemo';
import QueryDemo from './demos/QueryDemo';

// Real demo app backing handbook/frontend/react-state-management.md
// (F-120). Every demo pairs a "count" consumer and a "name" consumer
// so the same click (change count) can be checked against BOTH
// consumers' render counters — see README.md for the captured evidence.
function App() {
  return (
    <div className="app-root">
      <h1>React State Management Landscape — F-120</h1>

      <section>
        <h2>Context (no selector — both consumers re-render on any change)</h2>
        <ContextDemo />
      </section>

      <section>
        <h2>Redux Toolkit (selector-based — only the changed slice's consumer re-renders)</h2>
        <ReduxDemo />
      </section>

      <section>
        <h2>Zustand (same selective re-render guarantee, far less boilerplate)</h2>
        <ZustandDemo />
      </section>

      <section>
        <h2>TanStack Query (server state — cache dedup across independent consumers)</h2>
        <QueryDemo />
      </section>
    </div>
  );
}

export default App;
