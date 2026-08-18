import './App.css';
import BoundaryRecoveryDemo from './demos/BoundaryRecoveryDemo';
import GranularBoundariesDemo from './demos/GranularBoundariesDemo';
import EventHandlerErrorDemo from './demos/EventHandlerErrorDemo';

// Real demo app backing handbook/frontend/react-error-boundaries.md
// (F-115). Verified live in a browser, including a real render-phase
// crash caught and recovered from, and a real event-handler error
// PROVEN uncaught by the boundary.
function App() {
  return (
    <div className="app-root">
      <h1>React Error Handling — F-115</h1>
      <BoundaryRecoveryDemo />
      <GranularBoundariesDemo />
      <EventHandlerErrorDemo />
    </div>
  );
}

export default App;
