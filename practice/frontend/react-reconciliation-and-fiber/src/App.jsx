import './App.css';
import DomNodeReuseDemo from './demos/DomNodeReuseDemo';
import TypeChangeRemountDemo from './demos/TypeChangeRemountDemo';
import BatchingDemo from './demos/BatchingDemo';

// Real demo app backing handbook/frontend/react-reconciliation-and-fiber.md
// (F-112). Verified live in a browser, including direct DOM node identity checks.
function App() {
  return (
    <div className="app-root">
      <h1>React Reconciliation &amp; Fiber — F-112</h1>
      <DomNodeReuseDemo />
      <TypeChangeRemountDemo />
      <BatchingDemo />
    </div>
  );
}

export default App;
