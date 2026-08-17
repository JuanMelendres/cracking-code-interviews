import './App.css';
import FormResetBugDemo from './demos/FormResetBugDemo';
import CounterStaleReadDemo from './demos/CounterStaleReadDemo';
import UseToggleDemo from './demos/UseToggleDemo';
import DebouncedSearchDemo from './demos/DebouncedSearchDemo';

// Real demo app backing handbook/frontend/react-usereducer-and-custom-hooks.md
// (F-109: useReducer, F-110: custom hooks). Verified live in a browser.
function App() {
  return (
    <div className="app-root">
      <h1>React useReducer &amp; Custom Hooks — F-109 and F-110</h1>
      <FormResetBugDemo />
      <CounterStaleReadDemo />
      <UseToggleDemo />
      <DebouncedSearchDemo />
    </div>
  );
}

export default App;
