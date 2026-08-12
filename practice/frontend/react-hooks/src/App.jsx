import './App.css';
import EffectDependencyDemo from './demos/EffectDependencyDemo';
import EffectCleanupDemo from './demos/EffectCleanupDemo';
import StaleClosureDemo from './demos/StaleClosureDemo';
import RefDomAccessDemo from './demos/RefDomAccessDemo';
import RefMutableValueDemo from './demos/RefMutableValueDemo';

// Real demo app backing handbook/frontend/react-hooks-useeffect-and-useref.md
// (F-105: useEffect, F-106: useRef). Verified live in a browser, not described.
function App() {
  return (
    <div className="app-root">
      <h1>React Hooks — F-105 (useEffect) and F-106 (useRef)</h1>
      <EffectDependencyDemo />
      <EffectCleanupDemo />
      <StaleClosureDemo />
      <RefDomAccessDemo />
      <RefMutableValueDemo />
    </div>
  );
}

export default App;
