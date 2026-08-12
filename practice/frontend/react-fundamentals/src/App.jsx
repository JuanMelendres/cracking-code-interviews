import './App.css';
import JsxBasics from './demos/JsxBasics';
import PropsAndComposition from './demos/PropsAndComposition';
import CounterState from './demos/CounterState';
import EventsAndConditional from './demos/EventsAndConditional';
import ListKeysPitfall from './demos/ListKeysPitfall';

// Real demo app backing handbook/frontend/react-fundamentals-jsx-components-props-and-state.md
// (F-101 through F-104). Each section below is an independently runnable demo,
// verified live in a browser, not just described.
function App() {
  return (
    <div className="app-root">
      <h1>React Fundamentals — F-101 to F-104</h1>
      <JsxBasics />
      <PropsAndComposition />
      <CounterState />
      <EventsAndConditional />
      <ListKeysPitfall />
    </div>
  );
}

export default App;
