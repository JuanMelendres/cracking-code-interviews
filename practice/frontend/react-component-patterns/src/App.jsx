import './App.css';
import CompositionVsInheritanceDemo from './demos/CompositionVsInheritanceDemo';
import WindowWidthPatternsDemo from './demos/WindowWidthPatternsDemo';
import CompoundComponentsDemo from './demos/CompoundComponentsDemo';

// Real demo app backing handbook/frontend/react-component-patterns.md (F-111).
// Verified live in a browser, including a real window resize.
function App() {
  return (
    <div className="app-root">
      <h1>React Component Patterns — F-111</h1>
      <CompositionVsInheritanceDemo />
      <WindowWidthPatternsDemo />
      <CompoundComponentsDemo />
    </div>
  );
}

export default App;
