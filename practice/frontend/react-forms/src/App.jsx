import './App.css';
import ControlledVsUncontrolledDemo from './demos/ControlledVsUncontrolledDemo';
import ValidationTimingDemo from './demos/ValidationTimingDemo';
import RhfZodDemo from './demos/RhfZodDemo';

// Real demo app backing handbook/frontend/react-forms.md (F-114).
// Verified live in a browser, including a real react-hook-form + zod
// schema validation flow.
function App() {
  return (
    <div className="app-root">
      <h1>React Forms — F-114</h1>
      <ControlledVsUncontrolledDemo />
      <ValidationTimingDemo />
      <RhfZodDemo />
    </div>
  );
}

export default App;
